# Phase 0 Research: CSV-to-PostgreSQL Persistence Migration

## 1. JDBC driver & connection pooling

**Decision**: `org.postgresql:postgresql` JDBC driver + HikariCP for pooling.

**Rationale**: HikariCP is the de facto standard lightweight connection pool for JDBC-based Java
apps, satisfies the spec's connection-pooling and error-recovery requirements (FR-004) with
built-in retry/validation, and adds a single small dependency rather than a full framework.

**Alternatives considered**: A JPA/Hibernate ORM was rejected — it would pull in a large
dependency and encourage entity-mapping changes that risk touching more of the codebase than
necessary; plain JDBC keeps the change contained to the three `*Database` classes and their new
`persistence` helpers, matching the "don't break core logic" directive.

## 2. Schema migrations

**Decision**: Flyway, with plain SQL migration files under `src/main/resources/db/migration/`.

**Rationale**: Versioned SQL migrations are auditable, run identically against both PostgreSQL and
the H2 test database (using ANSI-compatible SQL types only — no PostgreSQL-only syntax), and give
a clear, reviewable history of schema changes for a team project.

**Alternatives considered**: Hand-rolled "create table if not exists" code was rejected — no
migration history, harder to reason about in code review (Principle II).

## 3. Test-tier database

**Decision**: When no `DATABASE_URL` (or equivalent config) is set, `DataSourceProvider`
transparently falls back to an in-memory H2 database running in PostgreSQL-compatibility mode.
Existing tests continue to call `UserDatabase`, `PlayerDatabase`, and `FriendDatabase` exactly as
before, unaware of which engine is behind them.

**Rationale**: This satisfies FR-007 ("tests must not require a shared/production database") and
the spec's explicit constraint ("in-memory DB for unit tests, Postgres for integration tests")
while requiring **zero changes to existing test files** — the swap is entirely internal to the
`*Database` classes' construction of their `DataSource`.

**Alternatives considered**: Testcontainers (spinning up real Postgres in Docker for every test
run) was rejected as the *default* for unit tests — it would require Docker to be running just to
execute `mvn test`, which the current CSV-based tests never needed, violating the "don't break
existing workflows" directive. Testcontainers remains a good option for a *separate*,
Docker-gated integration test suite added later (not required to satisfy this spec) but is not
used to run the existing unit tests.

## 4. Local development & integration environment

**Decision**: `docker-compose.yml` at the repo root defines a `postgres:16` service (with a named
volume for persistence) and the application's build/run step, matching the spec's Docker Compose
success criterion.

**Rationale**: Gives every team member and CI (once wired up) an identical, one-command way to get
a real Postgres instance running locally (SC-003).

## 5. TLS in transit

**Decision**: The Postgres service in `docker-compose.yml` is configured with `ssl = on` and a
locally-generated self-signed certificate; the JDBC URL used by `DataSourceProvider` sets
`sslmode=require`. At-rest encryption is explicitly not configured, per the spec's clarified scope
(FR-010).

**Rationale**: Matches the spec's clarified requirement exactly — encrypts the wire, doesn't add
at-rest infrastructure the course project doesn't need.

## 6. Structured logging

**Decision**: SLF4J API + Logback backend, used only inside the new/modified persistence code
(the three `*Database` classes' internals and the new `persistence` subpackage) to log connection
failures, slow queries (a simple execution-time threshold check), and migration errors, per FR-012.

**Rationale**: SLF4J + Logback is the standard, low-friction logging pair for JVM projects; scoping
it to the persistence layer avoids a project-wide logging refactor that isn't part of this
feature's stated scope.

**Alternatives considered**: Continuing to use `e.printStackTrace()` (today's pattern in all three
classes) was rejected because it doesn't satisfy FR-012's structured-logging requirement.
`java.util.logging` (built into the JDK, zero new dependency) was considered as an alternative to
avoid adding SLF4J/Logback; SLF4J+Logback was preferred for more usable structured output, but this
is a low-stakes choice either way.

## 7. One-time CSV → database migration

**Decision**: A standalone runnable class, `CsvToPostgresMigrator` (under
`src/main/java/.../p3/tools/`), that reads `userdata.csv`, `playerdata.csv`, and `friends.csv`
using the *existing* CSV-parsing logic already in the current `*Database` classes (extracted, not
rewritten) and calls the new PostgreSQL-backed `saveUser` / `savePlayer` / `addFriend` methods to
populate the database — reusing the real write path rather than a separate bespoke inserter, so
migrated data is guaranteed to be valid for the new schema. Malformed or orphaned rows are logged
(per FR-012) and skipped rather than aborting the whole run, per spec FR-005 / edge cases.

**Rationale**: Reusing the production save path (instead of writing raw SQL `INSERT`s) means the
migration can't produce data the application itself wouldn't consider valid, and keeps the
migration logic small.

## 8. Identity & uniqueness rules

**Decision**: Preserve current behavior exactly — the database enforces a `PRIMARY KEY` on
`user_id` only. No new `UNIQUE` constraint is added on `username` or `email`, because the existing
CSV-backed `saveUser`/`savePlayer` implementations never enforced one (a user could already be
saved twice under the same username with different IDs).

**Rationale**: The user explicitly asked that no core logic or behavior change — adding a
uniqueness constraint that didn't exist before would reject writes that used to silently succeed,
which is a behavior change and could break existing tests that reuse fixed usernames across runs.
This was flagged as a deferred, low-impact item during `/speckit-clarify` and is resolved here by
choosing exact parity with existing behavior.

**Alternatives considered**: Adding `UNIQUE` constraints as a "data quality improvement" was
rejected — out of scope for a like-for-like migration and risks silently breaking currently-passing
tests or legitimate (if messy) existing data.

## 9. Connection pool sizing & indexing

**Decision**: HikariCP pool sized modestly (default maximum ~10 connections, configurable), with
B-tree indexes on `users.username`, `users.email`, and `player_game_stats.user_id` to support the
existing lookup methods (`getUserByUsername`, `getUserByEmail`, `getPlayerByUserID`) efficiently at
the 10,000+-account scale target (FR-011). Indexes are purely additive for performance and do not
change query results or behavior.

**Rationale**: Matches actual read patterns already present in the codebase; avoids
over-provisioning for a course project while still meeting the stated scale target.
