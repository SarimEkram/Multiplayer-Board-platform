# Quickstart: Validating the PostgreSQL Migration

Prerequisites: Docker (for local Postgres via Compose), Java 23.0.2, the project's existing Maven
setup. No new tooling is required to run the existing unit test suite.

## 1. Run the existing test suite (no setup required)

```sh
mvn test
```

**Expected outcome**: Every existing test under `test/` passes unmodified — `DataSourceProvider`
automatically falls back to the in-memory H2 (Postgres-compatibility mode) database since no
`DATABASE_URL` is set, exactly as described in [research.md](./research.md) §3. This proves FR-006
and FR-007.

## 2. Start a real PostgreSQL instance locally

```sh
docker compose up -d
```

**Expected outcome**: A `postgres:16` container starts, reachable at `localhost:5432`, with TLS
enabled per [research.md](./research.md) §5. This proves the Docker Compose success criterion.

## 3. Run the schema migrations

Migrations run automatically via `MigrationRunner` the first time the application (or the CSV
migration tool) connects with `DATABASE_URL` pointed at the Compose Postgres instance. To confirm
manually:

```sh
mvn exec:java -Dexec.mainClass="ca.ucalgary.groupprojectgui.p3.tools.MigrationCheck"
```

**Expected outcome**: The `users`, `players`, `player_game_stats`, `friendships`, and
`game_history` tables exist, matching [data-model.md](./data-model.md).

## 4. Migrate existing CSV data

```sh
mvn exec:java -Dexec.mainClass="ca.ucalgary.groupprojectgui.p3.tools.CsvToPostgresMigrator"
```

**Expected outcome**: Every account in `userdata.csv`, every player's stats in `playerdata.csv`,
and every friendship in `friends.csv` now exist in the database, reachable via the same
`getUserById` / `getPlayerByUserID` / `getFriends` calls the application already uses. Any
malformed/orphaned row is logged (per FR-012), not silently dropped, and does not abort the run.
This proves FR-005 and User Story 3.

## 5. Run the application against the database

```sh
DATABASE_URL=jdbc:postgresql://localhost:5432/p3?sslmode=require mvn javafx:run
```

**Expected outcome**: Log in as one of the seeded `user1`–`user40` accounts (password `123456`,
per the project README), play a full game of any of the three games, check the leaderboard, and
add/remove a friend — every flow behaves identically to the CSV-based version. This proves User
Story 1 (SC-001, SC-002).

## 6. Confirm no manual CSV rollback is needed

Re-run `mvn test` a second time immediately after step 1, with no manual file cleanup in between.

**Expected outcome**: Tests pass again with no leftover state to reset, unlike the CSV-based
workflow the project's README currently warns about. This proves User Story 2.
