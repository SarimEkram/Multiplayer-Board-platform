

#  Online Multiplayer Game Platform
**Team Project – SENG 300 (Winter 2025)**  
**Group Number: P20**

---

##  Requirements

- Java JDK **23.0.2**
- JavaFX **23.0.2**
- Docker (only needed to run a real PostgreSQL instance locally — see below)

---

##  How to Run

1. After pulling the latest version from the repository, **rebuild the project**.
2. **Running the automated tests** (`mvn test` or your IDE's test runner) needs no setup at all —
   they run against a clean, in-memory database automatically. No CSV files to roll back anymore.
3. **Running the app locally against a real database**:
   - Generate a one-time local dev TLS certificate: `db/certs/generate-dev-cert.sh`
   - Start PostgreSQL: `docker compose up -d`
   - Migrate any existing player data (see step 5 below), or just register a new account
   - Set `DATABASE_URL=jdbc:postgresql://localhost:5432/p3?sslmode=require`,
     `DATABASE_USER=p3`, and `DATABASE_PASSWORD=p3` as environment variables (or JVM system
     properties) before launching the app
   - Launch the game by running the `MainApplication` file located at:
     `src/main/java/ca/ucalgary/groupprojectgui/p3/MainApplication.java`
   - If none of the above environment variables are set, the app falls back to the same
     in-memory database the tests use — useful for a quick local check without Docker.
4. Initial Password for all user1 - user40 is 123456
5. **One-time data migration**: the original `userdata.csv`/`playerdata.csv`/`friends.csv` files
   are no longer in this repository (they've already been migrated — see git history if you need
   them). If you're bringing your own copies forward from an older checkout, see
   `specs/001-postgres-migration/quickstart.md` Step 3 to migrate them into the database.

---

##  Getting Started

When the application launches:

- You’ll be prompted to **log in** using a registered username and password.
- If you're a **new user**, click **Register** to create a new account using your email and a secure password.

Once logged in, users can:

- Select one of three games: **Tic Tac Toe**, **Connect 4**, or **Checkers**
- View and edit profile details
- Check rankings and win/loss stats
- Chat in real time during gameplay
- Review gameplay history

---

##  Game Objectives

This platform includes three classic games, redesigned for online multiplayer:

- **Tic Tac Toe**: Align 3 symbols in a row (horizontally, vertically, or diagonally).
- **Connect 4**: Drop discs to create a line of 4 (horizontal, vertical, or diagonal).
- **Checkers**: Capture all opponent’s pieces by jumping over them.

All games feature:

- Real-time **2-player matchmaking**
- Win/loss tracking
- Fully interactive **JavaFX GUI**

---

##  Feature Overview

###  User Management
- Secure registration/login with email & password
- Profile updates and account deletion
- Password reset support

###  Matchmaking & Gameplay
- Smart matchmaking based on skill level
- Session-based game state management
- Smooth transitions via modular controllers

###  In-Game Chat
- Real-time messaging between players during matches

###  Leaderboard
- MMR-based ranking system
- Tier levels: **Bronze**, **Silver**, **Gold**, **Diamond**

###  User Interface
- Built entirely in **JavaFX**
- Responsive, intuitive layout and navigation
- Animated transitions between views

###  Testing
- **JUnit 5** unit tests for core modules:
    - Game logic
    - Matchmaking & leaderboard
    - Authentication
    - Networking
- Covers edge cases and invalid input scenarios

---

##  Project Structure

### From the `src/` folder, the codebase is organized into clear, modular packages:

- **Authentication**: Handles user registration, login, password reset, and input validation.
- **gameLogic**: Contains the core gameplay mechanics and logic for **Tic Tac Toe**, **Connect 4**, and **Checkers**.
- **main**: Includes the **MainApplication** launcher and all JavaFX controllers for GUI navigation and screen transitions.
- **MatchmakingLeaderboard**: Manages game queues, MMR ranking, and leaderboard logic across all games.
- **networking**: Manages data exchange for matchmaking and player connectivity across games.

### From the `test/` Contains JUnit test cases for core modules:

- **Authentication**: Includes unit tests for user login, registration, password reset, and input validation logic.
- **gameLogic**: Verifies gameplay rules, move validation, and win/loss detection for all three games.
- **MatchmakingLeaderboard**: Tests matchmaking queue handling, MMR calculations, leaderboard updates, and player rankings.
- **networking**: Validates data exchange and connection-related functionality for multiplayer support.




###  Data Storage
Player, account, and friendship data is stored in a PostgreSQL database, accessed only through
`UserDatabase`, `PlayerDatabase`, and `FriendDatabase` (see
`specs/001-postgres-migration/data-model.md`). The original `playerdata.csv`, `userdata.csv`, and
`friends.csv` files have been migrated and removed from the repository (available in git history
if needed) — the running application no longer reads or writes them.

---

