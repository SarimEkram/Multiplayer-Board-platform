# Implementation Plan: CSV-to-PostgreSQL Persistence Migration

**Branch**: `001-postgres-migration` | **Date**: 2026-09-12 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-postgres-migration/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command; its definition describes the execution workflow.

## Summary

Replace the CSV-file persistence behind `UserDatabase`, `PlayerDatabase`, and `FriendDatabase`
with a PostgreSQL-backed implementation, without changing any of their public static method
signatures, without touching game logic, controllers, or existing test code, and without changing
observable player-facing behavior. The three classes keep the exact same API; only what happens
inside them changes — file I/O is replaced with SQL against a connection-pooled, transactional
PostgreSQL database. A one-time migration utility copies existing CSV data into the database. Unit
tests continue to run against an in-memory, Postgres-compatible database with no Docker
dependency; a real PostgreSQL instance (via Docker Compose) is used for local development and
integration testing.

## Technical Context

**Language/Version**: Java 23.0.2 (unchanged)

**Primary Dependencies**: Existing JavaFX 23.0.2 stack (unchanged) **plus**, additive only, for
this feature: `org.postgresql:postgresql` (JDBC driver), HikariCP (connection pooling), Flyway
(SQL schema migrations), H2 in PostgreSQL-compatibility mode (in-memory database, test scope
only), SLF4J + Logback (structured logging, scoped to the persistence layer per FR-012)

**Storage**: PostgreSQL 16 (dev/integration, via Docker Compose) for real usage; H2 in-memory
(PostgreSQL mode) automatically substituted when no external database is configured, so
`mvn test` / IDE test runs keep working exactly as they do today with zero setup

**Testing**: JUnit 5 (unchanged — no existing test file is modified); the existing
`UserDatabaseTest`, `PlayerDatabaseTest`, `FriendDatabaseTest`, and all tests that transitively
call these classes must pass unmodified against the new implementation

**Target Platform**: Same as today — a JavaFX desktop client running on a developer's machine,
now paired with a locally-run PostgreSQL instance (Docker Compose) instead of local CSV files

**Project Type**: Single desktop application project (unchanged structure; see Project Structure
below for the project's actual on-disk source layout, which differs from the Maven default)

**Performance Goals**: Common gameplay actions (login, move submission, game completion,
leaderboard update) complete within 1 second and no more than 20% slower than the CSV baseline
(spec FR-009 / SC-005), holding at 10,000+ accounts (SC-006)

**Constraints**:
- No changes to `gameLogic/`, `Authentication/` business classes (other than the three `*Database`
  classes' internals), `MatchmakingLeaderboard/` business classes, or any JavaFX controller
- No changes to any existing file under `test/`
- All connections to the database MUST use TLS in transit (spec FR-010); at-rest encryption is
  explicitly out of scope
- Must not introduce new data-integrity rules (e.g., new uniqueness constraints) beyond what the
  current CSV-backed implementation already enforces, since that would be a behavior change
- Concurrent stat updates for the same player (e.g., two simultaneous game completions) MUST be
  protected with row-level locking, not a bare transaction alone (spec FR-003 / Edge Cases; see
  contracts/database-api-contract.md "Concurrency guarantee")
- A `game_history` row MUST be recorded for every completed match (spec FR-013)

**Scale/Scope**: Schema, indexing, and connection pooling sized to comfortably support 10,000+
user accounts and their stats/friendships/game-history (spec FR-011); current real usage is ~40
seeded test accounts

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|---|---|---|
| I. Test Coverage (JUnit) | PASS | New internal helper classes (connection/pool provider, SQL mapping, migration runner) get their own JUnit 5 unit tests run against the in-memory test database (the project's persistence boundary), consistent with "mock only DB/CSV I/O." Existing black-box tests of the three `*Database` classes are left untouched and continue to serve as the regression suite. |
| II. Code Review via MR/PR | PASS (process gate, applies at merge time, not a design constraint) |
| III. Modular Package Structure | PASS, with a placement decision: shared JDBC/connection-pool/config code is added as a **subpackage** (`MatchmakingLeaderboard.persistence`) rather than a new top-level package, following the existing precedent that `Authentication` already depends on `MatchmakingLeaderboard` (see `UserDatabase`'s import of `MatchmakingLeaderboard.Player`/`PlayerDatabase`). `gameLogic` and `networking` are untouched and gain no new dependencies. |
| IV. Data Safety (Persistent Store) | **RESOLVED** — originally flagged FAIL: Principle IV (then "Data Safety (CSV Files)") named the three CSV files as the persistent store and required an amendment before introducing a new mechanism. Resolved 2026-09-12 via `/speckit-constitution` (v2.0.0 → v3.0.0): Principle IV now describes a relational database, accessed only through the `*Database` classes, as the persistent store, with CSVs demoted to a migration source/archival backup. This plan's design (single-source-of-truth `*Database` classes, atomic transactions, test-isolated database) already matches the amended principle. |

**Post-Design Re-check** (after Phase 1 data-model/contracts/quickstart): No new violations
introduced. The data model adds no foreign keys or uniqueness constraints beyond what existed in
the CSV files (Principle IV concern about scope creep avoided); the new `persistence` code stays
inside the `MatchmakingLeaderboard` subpackage as planned (Principle III); the frozen API contract
in `contracts/database-api-contract.md` confirms zero signature changes for existing callers. The
Principle IV constitution-amendment action item stands as the only open item, unchanged.

## Project Structure

### Documentation (this feature)

```text
specs/001-postgres-migration/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md         # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
│   └── database-api-contract.md
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

This project's actual source roots (per `p3.iml`, and confirmed by the existing directory layout)
are `src/` and `test/` directly — **not** the Maven-conventional `src/main/java` that `pom.xml`
declares. `pom.xml`'s `<sourceDirectory>` is not what the team currently builds against; the plan
follows the real, observed layout so generated tasks target files that actually exist and compile
in the IDE. This mismatch is pre-existing and out of scope to fix here.

```text
src/
├── Authentication/
│   ├── UserDatabase.java          # MODIFIED: same public API, PostgreSQL-backed internals
│   ├── FriendDatabase.java        # MODIFIED: same public API, PostgreSQL-backed internals
│   ├── User.java                  # UNCHANGED
│   └── ...                        # UNCHANGED (business/service classes)
├── MatchmakingLeaderboard/
│   ├── PlayerDatabase.java        # MODIFIED: same public API, PostgreSQL-backed internals
│   ├── persistence/                # NEW subpackage — shared, low-level persistence utilities
│   │   ├── DataSourceProvider.java    # HikariCP DataSource selection (Postgres vs. embedded H2)
│   │   ├── DatabaseConfig.java        # Reads DB connection config from environment/system props
│   │   └── MigrationRunner.java       # Runs Flyway migrations at startup
│   ├── GameProcessor.java         # MODIFIED (additive only): one new recordGameHistory(...) call
│   │                               #   added alongside each existing savePlayer(...) call (FR-013)
│   └── ...                        # UNCHANGED (all other Matchmaking/Leaderboard business classes)
├── gameLogic/                     # UNCHANGED — no dependency on persistence added
├── networking/                    # UNCHANGED
└── main/
    ├── java/ca/ucalgary/groupprojectgui/p3/
    │   ├── MainApplication.java   # MODIFIED: triggers MigrationRunner at startup
    │   ├── controllers/           # UNCHANGED
    │   └── tools/
    │       └── CsvToPostgresMigrator.java  # NEW — one-time CSV → DB migration CLI (FR-005)
    └── resources/
        └── db/migration/          # NEW — Flyway SQL migration scripts
            ├── V1__init_schema.sql
            └── ...

test/
├── Authentication/                # UNCHANGED files; run unmodified against new implementation
├── MatchmakingLeaderboard/        # UNCHANGED files; run unmodified against new implementation
└── MatchmakingLeaderboard/persistence/   # NEW — unit tests for the new persistence utilities

docker-compose.yml                 # NEW — postgres:16 service + app service for local dev
```

**Structure Decision**: Single project (existing layout retained as-is). Only the three
`*Database` classes gain new internals; one new subpackage (`MatchmakingLeaderboard.persistence`)
holds shared connection/migration utilities; one new CLI tool performs the one-time CSV import;
Flyway SQL files and a `docker-compose.yml` are added at their conventional locations. No other
package, controller, or test file is touched.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

No open violations. Principle IV was amended (v3.0.0) to match this feature's design; see
Constitution Check above.
