---

description: "Task list for CSV-to-PostgreSQL Persistence Migration"
---

# Tasks: CSV-to-PostgreSQL Persistence Migration

**Input**: Design documents from `/specs/001-postgres-migration/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/database-api-contract.md, quickstart.md

**Tests**: Not explicitly requested as full TDD in the spec, but the project constitution
(Principle I) requires JUnit 5 unit tests for new classes in `Authentication` and
`MatchmakingLeaderboard`. Targeted test tasks are included for the **new** persistence
infrastructure and the CSV migration tool. The three existing `*Database` test files are
**never modified** — running them unmodified against the new implementation is itself the
primary regression check (see T016).

**Organization**: Tasks are grouped by user story (from spec.md) to enable independent
implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)

## Path Conventions

Single project. Real source roots are `src/` and `test/` at the repository root (not the
Maven-conventional `src/main/java`/`src/test/java` — see plan.md's Project Structure note).

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Bring in the new dependencies and scaffolding needed before any schema or code work.

- [ ] T001 Add `org.postgresql:postgresql`, HikariCP, Flyway (`flyway-core`), H2, `slf4j-api`, and
      `logback-classic` dependencies to `pom.xml`
- [ ] T002 [P] Create `docker-compose.yml` at the repository root with a `postgres:16` service
      (named volume, TLS enabled per research.md §5) and an app service
- [ ] T003 [P] Create the `src/main/resources/db/migration/` directory for Flyway SQL migrations

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core persistence infrastructure that every user story's implementation depends on.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [ ] T004 Write `src/main/resources/db/migration/V1__init_schema.sql` creating the `users`,
      `players`, `player_game_stats`, `friendships`, and `game_history` tables per
      data-model.md, including indexes on `users.username`, `users.email`, and
      `player_game_stats.user_id` per research.md §9
- [ ] T005 [P] Implement `DatabaseConfig` in
      `src/MatchmakingLeaderboard/persistence/DatabaseConfig.java` — reads `DATABASE_URL` (or
      equivalent config) from the environment; reports "not configured" when absent so
      `DataSourceProvider` can fall back to embedded H2
- [ ] T006 [P] Implement `DataSourceProvider` in
      `src/MatchmakingLeaderboard/persistence/DataSourceProvider.java` — builds a HikariCP-pooled
      `DataSource` against PostgreSQL (with `sslmode=require`) when `DatabaseConfig` reports a
      configured database, otherwise against an in-memory H2 instance in PostgreSQL-compatibility
      mode (research.md §3)
- [ ] T007 Implement `MigrationRunner` in
      `src/MatchmakingLeaderboard/persistence/MigrationRunner.java` — runs the Flyway migrations
      in `db/migration` against the `DataSource` from T006 (depends on T004, T006)
- [ ] T008 [P] Add `src/main/resources/logback.xml` configuring SLF4J/Logback for structured log
      output (connection failures, slow queries, migration errors) per FR-012
- [ ] T009 [P] Unit tests for `DataSourceProvider`/`DatabaseConfig` fallback behavior (no
      `DATABASE_URL` → H2; configured → Postgres-style URL parsing) in
      `test/MatchmakingLeaderboard/persistence/DataSourceProviderTest.java`
- [ ] T010 Wire `MigrationRunner.run()` into application startup in
      `src/main/java/ca/ucalgary/groupprojectgui/p3/MainApplication.java` (depends on T007)

**Checkpoint**: Database connectivity, schema, and logging are in place. User story
implementation can now begin.

---

## Phase 3: User Story 1 - Uninterrupted Gameplay After the Switch (Priority: P1) 🎯 MVP

**Goal**: Swap the internals of `UserDatabase`, `PlayerDatabase`, and `FriendDatabase` from CSV
file I/O to PostgreSQL, with zero change to their public API and zero change to existing tests.

**Independent Test**: Run the existing JUnit suite unmodified against the new implementation, and
manually complete a login → play a full game → view stats/leaderboard → add/remove a friend flow,
confirming results match the CSV-based version (quickstart.md Step 5).

### Implementation for User Story 1

- [ ] T011 [US1] Rewrite `UserDatabase` internals in `src/Authentication/UserDatabase.java` to
      read/write through `DataSourceProvider` (the `users` table) instead of `userdata.csv`,
      preserving every existing public method signature exactly (per
      contracts/database-api-contract.md)
- [ ] T012 [US1] Rewrite `PlayerDatabase` internals in
      `src/MatchmakingLeaderboard/PlayerDatabase.java` to read/write through `DataSourceProvider`
      (the `players` and `player_game_stats` tables) instead of `playerdata.csv`, preserving every
      existing public method signature exactly
- [ ] T013 [US1] Rewrite `FriendDatabase` internals in `src/Authentication/FriendDatabase.java` to
      read/write through `DataSourceProvider` (the `friendships` table) instead of `friends.csv`,
      preserving every existing public method signature exactly
- [ ] T014 [US1] Protect `PlayerDatabase.savePlayer`'s read-modify-write against concurrent
      updates to the same player by taking a row lock (`SELECT ... FOR UPDATE` on the
      `players`/`player_game_stats` rows for that `user_id`) inside the same transaction as the
      subsequent write — a bare transaction alone does not prevent a lost update between two
      concurrent `savePlayer` calls (see contracts/database-api-contract.md "Concurrency
      guarantee"). Wrap `FriendDatabase.addFriend`/`removeFriend`'s two-row mutual writes in a
      single transaction. Satisfies FR-003 (depends on T012, T013)
- [ ] T014a [US1] Add `PlayerDatabase.recordGameHistory(GameType gameType, int playerOneId, int
      playerTwoId, Integer winnerId)` in `src/MatchmakingLeaderboard/PlayerDatabase.java`, writing
      one row to `game_history`, and call it from `src/MatchmakingLeaderboard/GameProcessor.java`
      alongside its existing `PlayerDatabase.savePlayer(...)` calls (currently at lines 59-60 and
      135-136) so every completed match is captured. Satisfies FR-013 (depends on T012)
- [ ] T015 [US1] Add retry/clear-error-state handling for temporary database connectivity loss in
      `DataSourceProvider` (`src/MatchmakingLeaderboard/persistence/DataSourceProvider.java`),
      used by all three rewritten `*Database` classes, per FR-004
- [ ] T016 [US1] Run the full existing test suite —
      `test/Authentication/UserDatabaseTest.java`, `test/Authentication/FriendDatabaseTest.java`,
      `test/MatchmakingLeaderboard/PlayerDatabaseTest.java`, and every test that transitively
      exercises these classes (matchmaking, leaderboard, controller tests) — unmodified against
      the new implementation, and fix internals until all pass (depends on T011-T015, T014a)
- [ ] T017 [US1] Manually validate quickstart.md Step 5 against the Docker Compose Postgres
      instance: log in as a seeded `user1`–`user40` account, play one full game of each of the
      three games, check the leaderboard, and add/remove a friend (depends on T016)

**Checkpoint**: User Story 1 is fully functional and testable independently — gameplay is
behaviorally identical to the CSV-based version.

---

## Phase 4: User Story 2 - Reliable Local Development Environment (Priority: P2)

**Goal**: A team member can start the full application (database + platform) with one documented
step and run tests repeatedly with no manual CSV rollback.

**Independent Test**: From a clean checkout, follow the documented setup once, then run the test
suite twice in a row with no manual cleanup in between (quickstart.md Steps 1-2 and 6).

### Implementation for User Story 2

- [ ] T018 [US2] Finalize `docker-compose.yml` (health check on the `postgres` service, app
      service depends_on the healthy database, named volume for data persistence across restarts)
- [ ] T019 [US2] Update `README.md`'s "How to Run" section: replace the CSV-rollback instruction
      with the new one-command local setup (`docker compose up -d`, run migrations, run the app),
      per quickstart.md
- [ ] T020 [US2] Confirm the H2 in-memory fallback path (T006) leaves no state between separate
      `mvn test` invocations, so no manual data reset is ever required for the test suite
- [ ] T021 [US2] Validate quickstart.md Steps 1, 2, and 6 end-to-end on a clean checkout (depends
      on T017, T018, T019, T020)

**Checkpoint**: User Stories 1 and 2 both work independently — a new team member can be running
locally in under 10 minutes with no CSV file management.

---

## Phase 5: User Story 3 - No Player Data Lost in the Switch (Priority: P3)

**Goal**: A one-time migration procedure moves all existing CSV data into the database without
loss, and reports (rather than silently drops) malformed or orphaned rows.

**Independent Test**: Run the migration tool against a copy of the current CSV files, then compare
record counts and spot-check field values between the CSV source and the migrated database
(quickstart.md Step 4).

### Implementation for User Story 3

- [ ] T022 [P] [US3] Implement `CsvToPostgresMigrator` in
      `src/main/java/ca/ucalgary/groupprojectgui/p3/tools/CsvToPostgresMigrator.java`, reusing
      the existing CSV-parsing logic from the pre-migration `UserDatabase`/`PlayerDatabase`/
      `FriendDatabase` (extracted, not rewritten) and writing through the new
      `saveUser`/`savePlayer`/`addFriend` methods from T011-T013 (depends on T011, T012, T013)
- [ ] T023 [US3] Add malformed/orphaned-row detection to `CsvToPostgresMigrator` — log via SLF4J
      (T008) and skip the row rather than aborting the run, per FR-005 and the spec's edge cases
      (depends on T022)
- [ ] T024 [US3] Add an idempotency guard to `CsvToPostgresMigrator` so re-running it does not
      create duplicate accounts/records or corrupt already-migrated data (depends on T022)
- [ ] T025 [P] [US3] Unit tests for `CsvToPostgresMigrator` (malformed-row handling, idempotent
      re-run) in `test/tools/CsvToPostgresMigratorTest.java` (depends on T022-T024)
- [ ] T026 [US3] Validate quickstart.md Step 4 against the real seeded `userdata.csv`/
      `playerdata.csv`/`friends.csv`, spot-checking values per spec User Story 3's Acceptance
      Scenario 1 (depends on T023, T024)

**Checkpoint**: All three user stories are independently functional — existing player data is
fully and safely migrated.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final validation and the governance follow-up this migration requires.

- [ ] T027 [P] Measure common-action latency (login, move submission, match completion,
      leaderboard update) against SC-005 (≤1s, ≤20% slower than CSV baseline) and SC-006 (holds
      with 10,000+ synthetic accounts loaded)
- [ ] T028 Draft and apply a constitution amendment updating Principle IV ("Data Safety — CSV
      Files") and the Technology & Data Constraints section to describe PostgreSQL (via the
      `*Database` classes) as the source of truth, per the Constitution Check finding in plan.md
      (use `/speckit-constitution`) — required before this feature merges to `main`
- [ ] T029 [P] Archive the original `userdata.csv`, `playerdata.csv`, and `friends.csv` as a
      backup (per the spec's cutover Assumption) once T026's data-integrity check passes
- [ ] T030 Run the full quickstart.md validation guide end-to-end (Steps 1-6) as final sign-off

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately
- **Foundational (Phase 2)**: Depends on Setup — BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational only
- **User Story 2 (Phase 4)**: Depends on Foundational; T021 also depends on US1's T017 (validating
  the app runs against Docker Compose Postgres)
- **User Story 3 (Phase 5)**: Depends on Foundational; T022 also depends on US1's T011-T013 (needs
  the PostgreSQL-backed save methods to write through)
- **Polish (Phase 6)**: Depends on all three user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: No dependency on US2/US3 — the core deliverable
- **User Story 2 (P2)**: Builds on US1 being runnable against real Postgres (T017) to validate the
  local dev workflow, but its own docs/compose work (T018, T019) can start in parallel with US1
- **User Story 3 (P3)**: Needs US1's rewritten save methods (T011-T013) to write through, so its
  implementation tasks start after those land, even though T022 is marked [P] relative to other
  US3 tasks

### Within Each User Story

- Foundational persistence utilities before any `*Database` rewrite
- `*Database` rewrites before transaction-wrapping and retry logic
- Implementation before the "run existing tests unmodified" checkpoint
- Checkpoint validation before moving to the next priority

### Parallel Opportunities

- T002, T003 (Setup) can run in parallel
- T005, T006, T008, T009 (Foundational) can run in parallel; T004 (schema SQL) can also run in
  parallel with these since it touches a different file
- T011, T012, T013 (US1) touch three different files and can run in parallel
- T022 and T025 (US3) are marked [P] relative to each other's file scope
- T027 and T029 (Polish) can run in parallel

---

## Parallel Example: User Story 1

```bash
# Launch the three *Database rewrites together (different files, no shared state):
Task: "Rewrite UserDatabase internals in src/Authentication/UserDatabase.java"
Task: "Rewrite PlayerDatabase internals in src/MatchmakingLeaderboard/PlayerDatabase.java"
Task: "Rewrite FriendDatabase internals in src/Authentication/FriendDatabase.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (blocks everything else)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: run the existing test suite unmodified + manual gameplay pass
5. This alone delivers the core promise: identical gameplay, PostgreSQL-backed

### Incremental Delivery

1. Setup + Foundational → connectivity and schema ready
2. User Story 1 → identical gameplay on Postgres (MVP)
3. User Story 2 → one-command local dev, no CSV rollback friction
4. User Story 3 → existing player data safely migrated
5. Polish → performance sign-off + constitution amendment (T028) before merge

---

## Notes

- [P] tasks touch different files with no dependency on an incomplete task
- [Story] label maps each task to its user story for traceability
- No existing file under `test/` is ever modified — verifying that remains true is itself part of
  T016's acceptance
- T028 (constitution amendment) has been completed — the constitution was amended to v3.0.0 via
  `/speckit-constitution` to redefine Principle IV around the new persistent store, resolving the
  gate finding from plan.md's Constitution Check. T028 is left in this list as a record of that
  step, not an open blocker.
- T014a and the rewritten T014 were added/sharpened after a `/speckit-analyze` pass found the
  `game_history` entity had no write-path task (now FR-013 + T014a) and that "wrap in a
  transaction" alone under-specified the concurrency guarantee FR-003 requires (now row-locking,
  T014)
