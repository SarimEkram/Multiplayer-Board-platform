# Feature Specification: CSV-to-PostgreSQL Persistence Migration

**Feature Branch**: `001-postgres-migration`

**Created**: 2026-09-12

**Status**: Draft

**Input**: User description: "Feature: Migrate from CSV persistence to PostgreSQL database

Current state: playerdata.csv, userdata.csv, friends.csv store all game data. Data layer is tightly coupled to CSV I/O.

Goal: Replace CSV files with PostgreSQL database. Decouple persistence from file I/O. Maintain all existing game logic, authentication, matchmaking unchanged. Foundation for future React frontend migration.

Scope:
- Migrate PlayerDatabase, UserDatabase, FriendDatabase to use PostgreSQL
- Add database schema (tables for users, players, friends, game_history, etc.)
- Keep all business logic in gameLogic/, Authentication/, MatchmakingLeaderboard/ packages unchanged
- Controllers continue to call existing service classes (no API layer change yet)
- JavaFX UI stays as-is
- All existing JUnit tests must pass with no changes to test code

Constraints:
- No changes to game logic or controller interfaces
- Database must support transactions (for atomic operations)
- Must handle connection pooling and error recovery
- Must be testable (in-memory DB for unit tests, Postgres for integration tests)

Success criteria:
- All tests pass
- Game play identical to CSV version (no behavior change)
- Can run locally with Docker Compose (Postgres + Java app)
- Performance no worse than CSV version
- Data migration from existing CSVs to database works

Why: Scalability, ACID compliance, modern data layer, enables React frontend migration in future."

## Clarifications

### Session 2026-09-12

- Q: Does the credential and gameplay data in the new database need to be encrypted in transit (TLS) and/or at rest, or is the current CSV-equivalent level of protection acceptable? → A: TLS in transit only (matches current local-trust model, no at-rest encryption requirement).
- Q: What concrete, measurable threshold should define "performance no worse than the CSV version" for common actions like login, move submission, and leaderboard updates? → A: Bounded regression: up to 20% slower per action is acceptable, and all actions must still complete within 1 second.
- Q: What scale of data and concurrent usage should the new database be designed to comfortably handle? → A: Design explicitly for large scale (10,000+ accounts) to fully realize the "scalability" motivation stated in the request.
- Q: Does this migration need to produce operational logging/monitoring for the new database layer? → A: Structured logging required for connection failures, slow queries, and migration errors; no dashboards/metrics/tracing yet.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Uninterrupted Gameplay After the Switch (Priority: P1)

As a returning player, after the platform's data layer is migrated, I can log in, play Tic Tac Toe,
Connect 4, or Checkers, see my correct stats/MMR/tier, and use friends and chat exactly as before —
with no visible change in behavior, correctness, or responsiveness.

**Why this priority**: This is the core promise of the migration — it is invisible to players. If
gameplay, login, or stats tracking regress in any way, the migration has failed regardless of how
clean the new data layer is internally.

**Independent Test**: Run the full existing JUnit suite unchanged against the new data layer, and
manually complete a login → play a full game → view updated stats/leaderboard → add/remove a friend
flow, confirming results match the CSV-based version.

**Acceptance Scenarios**:

1. **Given** a player with existing stats, **When** they log in after migration, **Then** their
   win/loss record, MMR, and tier are identical to what the CSV version reported.
2. **Given** two matched players, **When** they complete a game, **Then** the result is recorded and
   reflected in the leaderboard exactly as it would have been under the CSV-based system.
3. **Given** the existing automated test suite, **When** it is run against the migrated system,
   **Then** every test passes without any modification to test code.

---

### User Story 2 - Reliable Local Development Environment (Priority: P2)

As a team member working on the project, I can start the full application (data store + game
platform) on my machine with one documented setup step, without needing to hand-manage CSV files
or worry about test runs corrupting shared data.

**Why this priority**: The team currently has to manually roll back CSV files after test runs
(a known, error-prone step per the project README). Removing that friction is a major point of
this migration and directly affects day-to-day development velocity.

**Independent Test**: A team member who has never set up the project can follow one documented
procedure to get the database and application running locally, and can run the test suite
repeatedly without manually resetting any data files afterward.

**Acceptance Scenarios**:

1. **Given** a clean checkout of the repository, **When** a developer follows the documented local
   setup procedure, **Then** both the database and the application start successfully.
2. **Given** the automated test suite has just run, **When** a developer inspects the data store,
   **Then** no manual cleanup or file rollback is required before the next run.

---

### User Story 3 - No Player Data Lost in the Switch (Priority: P3)

As a platform owner, I can migrate all existing player accounts, stats, and friendships from the
current CSV files into the new database once, and confirm afterward that every account and its
data arrived intact.

**Why this priority**: Existing players (the seeded user1–user40 test accounts and any real
accumulated data) must not be silently dropped or corrupted during the one-time cutover. This is
lower priority than P1/P2 only because it is a one-time operational step rather than ongoing
behavior, but it is a hard gate before the migration can be considered complete.

**Independent Test**: Run the migration procedure against a copy of the current CSV files, then
compare record counts and spot-check field values (username, password hash, wins/losses, MMR,
tier, friendships) between the CSV source and the migrated database.

**Acceptance Scenarios**:

1. **Given** the current `userdata.csv`, `playerdata.csv`, and `friends.csv` files, **When** the
   migration procedure runs, **Then** every account, its stats, and its friendships appear in the
   database with matching values.
2. **Given** a malformed or duplicate row in a source CSV, **When** the migration procedure
   encounters it, **Then** the issue is reported clearly rather than silently dropped or causing
   the whole migration to fail without explanation.

---

### Edge Cases

- What happens when the database is temporarily unreachable when the application starts or during
  play? The system MUST surface a clear error/retry rather than crashing or silently losing data.
- How does the system handle two concurrent updates to the same player's stats (e.g., simultaneous
  game completions) so results aren't lost or double-counted? A transaction alone does not prevent
  this if the update reads the current stats, modifies them in memory, then writes the whole row
  back — the write MUST be protected against a concurrent update to the same row (e.g., row
  locking or an equivalent guarantee), not just wrapped in a transaction.
- What happens when the one-time CSV-to-database migration is run a second time (e.g., re-run by
  mistake)? It MUST NOT create duplicate accounts/records or corrupt already-migrated data.
- What happens when a CSV row references a friendship or player that doesn't exist (orphaned
  reference)? The migration MUST report it rather than silently importing broken data.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST persist all user accounts, player stats, and friendships in a relational
  database instead of the current CSV files.
- **FR-002**: System MUST preserve all existing game logic, authentication, and matchmaking/
  leaderboard behavior exactly as-is — no observable behavior change for players.
- **FR-003**: System MUST perform related data updates (e.g., recording a game result and updating
  stats/leaderboard) as a single atomic operation, so a failure partway through never leaves data
  in a half-updated state.
- **FR-004**: System MUST recover from a temporary loss of database connectivity with a clear error
  state and automatic retry/reconnection, rather than crashing the application.
- **FR-005**: System MUST provide a one-time migration procedure that transfers all existing data
  from `userdata.csv`, `playerdata.csv`, and `friends.csv` into the database without data loss, and
  that reports (rather than silently ignores) malformed or orphaned source rows.
- **FR-006**: The existing automated test suite MUST run unchanged (no test code modifications) and
  MUST pass against the migrated data layer.
- **FR-007**: System MUST support running automated tests without requiring a shared or
  production database instance, so test runs cannot corrupt real player data.
- **FR-008**: System MUST allow the full application (database + platform) to be started locally
  via a single documented procedure.
- **FR-009**: Common gameplay actions (login, submitting a move, completing a game, viewing
  leaderboard/stats) MUST complete within 1 second, and no more than 20% slower per action than
  the CSV-based baseline.
- **FR-010**: All connections between the application and the database MUST be encrypted in
  transit (TLS); encryption at rest is not required for this migration.
- **FR-011**: The database schema, indexing, and connection pooling MUST be designed to
  comfortably support at least 10,000 user accounts and their associated stats, friendships, and
  game history, even though current usage is far smaller.
- **FR-012**: System MUST produce structured log entries for database connection failures, slow
  queries, and migration errors; dashboards, metrics, and tracing are out of scope for this
  migration.
- **FR-013**: System MUST record a game history entry (participants, game type, result, and
  timestamp) whenever a match completes. Exposing this history in the UI is out of scope for this
  migration (see Assumptions) — only capturing the data is required.

### Key Entities

- **User Account**: Credentials and identity for a registered player (username, email, password
  hash). Maps to the current `userdata.csv`.
- **Player Profile**: A user's gameplay stats — wins, losses, MMR, tier. Maps to the current
  `playerdata.csv`.
- **Friendship**: A relationship between two user accounts. Maps to the current `friends.csv`.
- **Game History Record**: A record of a completed match (participants, game type, result,
  timestamp) — new entity not present in the current CSV files, introduced to support historical
  gameplay review and future reporting.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of the existing automated test suite passes, unmodified, against the migrated
  system.
- **SC-002**: Across a full manual regression pass of login, all three games, leaderboard, and
  friends features, players observe zero behavioral differences compared to the CSV-based version.
- **SC-003**: A team member unfamiliar with the new setup can get the full application running
  locally, from a clean checkout, in under 10 minutes using one documented procedure.
- **SC-004**: 100% of existing player accounts, stats, and friendships are present and correct
  after the one-time data migration, with zero unexplained data loss.
- **SC-005**: Common gameplay actions (login, move submission, match completion, leaderboard
  update) complete within 1 second each, and no more than 20% slower per action than the
  CSV-based baseline.
- **SC-006**: The above response-time targets (SC-005) continue to hold when the database
  contains at least 10,000 user accounts and their associated stats, friendships, and game
  history.

## Assumptions

- The migration is a one-time cutover: once the database is populated and verified, the CSV files
  are retained only as an archival backup and are no longer read or written by the running
  application.
- No new user-facing features are introduced by this migration — it is a like-for-like replacement
  of the persistence layer.
- The controllers, game logic, authentication, and matchmaking/leaderboard packages already
  interact with persistence only through the existing `PlayerDatabase`, `UserDatabase`, and
  `FriendDatabase` classes (per the project constitution's Data Safety and Modular Package Structure
  principles), so no other code needs to change to consume the new data layer.
- "Game history" is a new capability requested in scope (FR-013); since no existing UI currently
  displays match history beyond aggregate stats, exposing it in the UI is out of scope for this
  migration — only the underlying data capture is required.
- Local development/testing environment setup (e.g., container-based database provisioning) is
  acceptable as the standard way team members and CI run the application and tests going forward.
