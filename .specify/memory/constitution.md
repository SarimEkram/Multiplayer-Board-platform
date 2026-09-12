<!--
Sync Impact Report
- Version change: 2.0.0 → 3.0.0
- Modified principles:
  - "IV. Data Safety (CSV Files)" → replaced by "IV. Data Safety (Persistent Store)"
    (redefines the persistence mechanism from CSV files to a relational database
    accessed only through the `UserDatabase`/`PlayerDatabase`/`FriendDatabase` classes;
    CSV files are demoted to a one-time migration source / archival backup)
- Added sections: none
- Removed sections: none (Technology & Data Constraints updated in place, not removed)
- Follow-up TODOs:
  - TODO(RATIFICATION_DATE): original adoption date still unknown.
- Rationale for MAJOR bump: this redefines a Core Principle's non-negotiable rule
  (the persistence mechanism itself) rather than clarifying or extending it, and it
  reverses the prior principle's explicit requirement that no new persistence
  mechanism be introduced without an amendment — this amendment IS that required
  change, made in response to the `001-postgres-migration` feature.
-->

# Online Multiplayer Game Platform Constitution

## Core Principles

### I. Test Coverage (JUnit)
All classes in `gameLogic/`, `MatchmakingLeaderboard/`, and `Authentication` MUST have
JUnit 5 unit tests. Controllers require integration tests only, not unit tests. Tests
MUST mock database I/O only — internal application classes MUST NOT be mocked. All
tests MUST pass before a change is merged.
**Rationale**: Unit-testing the true business logic (game rules, MMR/leaderboard
calculation, authentication) catches regressions where they matter most, while mocking
only the I/O boundary keeps tests meaningful rather than trivially green.

### II. Code Review via MR/PR
All commits to `main` MUST go through a pull/merge request approved by at least one
team member who is not the author. CI MUST pass before merge. Merging without approval,
or with failing CI, is forbidden.
**Rationale**: A second reviewer and a green CI run are the team's baseline defense
against regressions and unreviewed changes reaching `main`.

### III. Modular Package Structure
The codebase MUST remain organized into five core packages: `gameLogic`,
`Authentication`, `MatchmakingLeaderboard`, `networking`, and `main`. `gameLogic` MUST
NOT import from `main` (JavaFX) or from `networking`. Controllers MUST contain no
business logic — no game rules, no MMR calculation. Communication between packages
MUST go through interfaces (e.g., `IGameMatchmaking`, `IGameLeaderboard`) rather than
concrete cross-package references.
**Rationale**: Keeping game rules free of JavaFX/networking dependencies makes them
testable in isolation and lets packages evolve independently; interface-based
communication prevents tight coupling between subsystems.

### IV. Data Safety (Persistent Store)
Only the `UserDatabase`, `PlayerDatabase`, and `FriendDatabase` classes may read or
write the platform's persistent store (a relational database). Controllers and game
logic MUST NOT perform database or file I/O directly. Multi-row or related updates
(e.g., recording a game result and updating leaderboard stats, or a mutual friend
add/remove) MUST be atomic (wrapped in a single transaction). Automated tests MUST be
able to run against an isolated, non-shared instance of the store (e.g., an in-memory
database) so a test run can never corrupt real player data, and never require manual
rollback afterward.
**Rationale**: Centralizing all persistence access in these three classes keeps the
data layer auditable and swappable regardless of the underlying storage technology;
requiring atomic multi-row updates and test isolation prevents both partial/corrupt
writes and the recurring test-data-rollback hazard the original CSV-file version of
this principle was written to address.

## Technology & Data Constraints

- Java **23.0.2** and JavaFX **23.0.2** are the required runtime/toolchain versions;
  the project MUST rebuild cleanly after pulling changes, per the README's build
  instructions.
- Persistent state lives in a relational database, accessed only through the
  `UserDatabase`, `PlayerDatabase`, and `FriendDatabase` classes (Principle IV); no
  further change of persistence mechanism may be introduced without a constitution
  amendment.
- The original `userdata.csv`, `playerdata.csv`, and `friends.csv` files are retained
  only as a one-time migration source and archival backup; the running application does
  not read from or write to them.
- All connections to the database MUST be encrypted in transit (TLS); at-rest
  encryption is not required.
- Networking code (`networking` package) MUST NOT assume a specific deployment
  environment beyond what is already used for matchmaking/session communication.

## Development Workflow

- Every pull/merge request targeting `main` MUST have one non-author approval and a
  passing CI run before it can be merged (Principle II).
- Changes to `gameLogic`, `MatchmakingLeaderboard`, or `Authentication` MUST include or
  update JUnit 5 unit tests; changes to controllers MUST include or update integration
  tests (Principle I).
- Automated tests MUST run against an isolated, non-shared database instance, so no
  manual data rollback or reset is ever required after running the test suite locally
  (Principle IV).
- Per team convention, commits and pushes go to the `github` remote, not `origin`.

## Governance

This constitution supersedes ad-hoc team practices for anything it addresses. Amendments
require:
1. A documented rationale for the change (what principle/section changes and why).
2. A version bump following semantic versioning: MAJOR for incompatible governance or
   principle removals/redefinitions, MINOR for new or materially expanded principles/
   sections, PATCH for clarifications and wording fixes.
3. Updating the `Last Amended` date below on every change.

Pull/merge requests and reviews for this project MUST verify compliance with the Core
Principles above, in particular Principle I (test coverage for game/matchmaking/auth
logic), Principle II (non-author approval + passing CI), and Principle IV (persistent
store data safety). Any deviation MUST be called out and justified in the PR description
rather than silently merged.

**Version**: 3.0.0 | **Ratified**: TODO(RATIFICATION_DATE): original adoption date unknown | **Last Amended**: 2026-09-12
