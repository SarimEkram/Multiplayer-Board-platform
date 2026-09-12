<!--
Sync Impact Report
- Version change: 1.0.0 → 2.0.0
- Modified principles:
  - "I. Modular Package Boundaries" → replaced by "III. Modular Package Structure"
    (adds explicit import restrictions and interface-based inter-package communication)
  - "II. Test-First for Game and Account Logic" → replaced by "I. Test Coverage (JUnit)"
    (scopes to gameLogic/MatchmakingLeaderboard/Authentication, adds controller
    integration-test rule and mocking policy)
  - "III. Player Data Integrity" → replaced by "IV. Data Safety (CSV Files)"
    (adds *Database-class-only write rule, atomic writes, no File I/O in
    controllers/game logic)
  - "IV. Consistent JavaFX User Experience" → removed (superseded by controller
    constraints folded into Principle III)
  - "V. Simplicity and Dependency Discipline" → removed (not carried forward by the team)
  - New: "II. Code Review via MR/PR" (was not previously covered)
- Added sections: none beyond principle set (Technology & Data Constraints and
  Development Workflow retained/updated to match new principles)
- Removed sections: none (JavaFX UX and dependency-discipline guidance folded out of
  Core Principles per team direction)
- Follow-up TODOs:
  - TODO(RATIFICATION_DATE): original adoption date still unknown; team did not supply
    one for this amendment either.
-->

# Online Multiplayer Game Platform Constitution

## Core Principles

### I. Test Coverage (JUnit)
All classes in `gameLogic/`, `MatchmakingLeaderboard/`, and `Authentication` MUST have
JUnit 5 unit tests. Controllers require integration tests only, not unit tests. Tests
MUST mock database/CSV I/O only — internal application classes MUST NOT be mocked. All
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

### IV. Data Safety (CSV Files)
Only `*Database` classes may write to `playerdata.csv`, `userdata.csv`, or
`friends.csv`. Controllers and game logic MUST NOT perform file I/O directly. All CSV
writes MUST be atomic. Any data mutated by local test runs MUST be rolled back before
committing.
**Rationale**: Centralizing CSV writes in dedicated classes prevents partial/corrupt
writes and keeps the persistence layer auditable; the README already flags stray
test-run mutations to player data as a recurring hazard.

## Technology & Data Constraints

- Java **23.0.2** and JavaFX **23.0.2** are the required runtime/toolchain versions;
  the project MUST rebuild cleanly after pulling changes, per the README's build
  instructions.
- Persistent state lives in the three CSV files listed in Principle IV, accessed only
  through their corresponding `*Database` classes; no new persistence mechanism may be
  introduced without a constitution amendment.
- Networking code (`networking` package) MUST NOT assume a specific deployment
  environment beyond what is already used for matchmaking/session communication.

## Development Workflow

- Every pull/merge request targeting `main` MUST have one non-author approval and a
  passing CI run before it can be merged (Principle II).
- Changes to `gameLogic`, `MatchmakingLeaderboard`, or `Authentication` MUST include or
  update JUnit 5 unit tests; changes to controllers MUST include or update integration
  tests (Principle I).
- Contributors MUST restore `playerdata.csv`/`userdata.csv`/`friends.csv` to their
  pre-test state after running the test suite locally, and MUST NOT commit test-mutated
  data files (Principle IV).
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
logic), Principle II (non-author approval + passing CI), and Principle IV (CSV data
safety). Any deviation MUST be called out and justified in the PR description rather
than silently merged.

**Version**: 2.0.0 | **Ratified**: TODO(RATIFICATION_DATE): original adoption date unknown | **Last Amended**: 2026-09-12
