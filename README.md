# Online Multiplayer Game Platform
*Team Project – SENG 300 (Winter 2025)*

*Group Number: P20*

## Requirements
- Java JDK 23.0.2
- JavaFX 23.0.2

---

## How to Run

The game can be launched by running MainApplication file which located in
"src/main/java/ca.ucalgary.groupprojectgui.p3/MainApplication"

## Getting Started

When you launch the application, the first screen will prompt you to **login using a registered username and password**.  
If you are a **new user**, click on the **Register** button to create a new account using your email and a secure password.

Once logged in, players can:
- Choose one of the three available games: **Tic Tac Toe**, **Connect 4**, or **Checkers**
- View and edit their profile details
- Check their leaderboard ranking and win/loss stats
- Use in-game **chat** for social interaction
- Track their previous games and gameplay history

---

## Game Objectives

This project implements three classic games in a multiplayer format:
- **Tic Tac Toe**: Form a line of 3 of your symbols (horizontally, vertically, or diagonally).
- **Connect 4**: Drop discs into a grid to make a vertical, horizontal, or diagonal line of 4.
- **Checkers**: Capture all of the opponent’s pieces by jumping over them.

Each game supports:
- Real-time two-player matchmaking
- Win/loss tracking
- Interactive GUI using JavaFX

---

## Feature Overview

### User Management
- Account registration and login with email and password
- Profile editing and account deletion
- Password reset functionality

### Matchmaking & Gameplay
- Intelligent matchmaking system that pairs users for any of the three games
- Game state is preserved per session with win/loss outcome tracking
- Modular controllers for each game allow smooth GUI transitions

### In-Game Chat
- Integrated chat for each match so users can communicate while playing

### Leaderboard
- Ranking system based on **MMR**
- Tiered rank levels (Bronze, Silver, Gold, Diamond) based on performance

### User-Friendly GUI
- Fully interactive graphical interface built using **JavaFX**
- Intuitive layout and navigation through mouse clicks
- Clean transitions between login, matchmaking, gameplay, and leaderboard screens

### Testing
- Unit tests included for all major modules (game logic, matchmaking and Leaderboard, authentication and Networking)
- Boundary cases and invalid input handling are covered using JUnit 5

---

## Project Structure

From the **src** folder, the codebase is organized into clear, modular packages:
- **Authentication**: Handles user registration, login, password reset, and input validation.
- **gameLogic**: Contains the core gameplay mechanics and logic for **Tic Tac Toe**, **Connect 4**, and **Checkers**.
- **main**: Includes the **MainApplication** launcher and all JavaFX controllers for GUI navigation and screen transitions.
- **MatchmakingLeaderboard**: Manages game queues, MMR ranking, and leaderboard logic across all games.
- **networking**: Manages data exchange for matchmaking and player connectivity across games.

From the **test** Contains JUnit test cases for core modules:
- **Authentication**: Includes unit tests for user login, registration, password reset, and input validation logic.
- **gameLogic**: Verifies gameplay rules, move validation, and win/loss detection for all three games.
- **MatchmakingLeaderboard**: Tests matchmaking queue handling, MMR calculations, leaderboard updates, and player rankings.
- **networking**: Validates data exchange and connection-related functionality for multiplayer support.

**playerdata.csv**: Stores player statistics including wins, losses, MMR, ranking points, and tier for all games.

**userdata.csv**: Contains registered user information such as usernames, emails, hashed passwords, and user IDs.

**friends.csv**: Maintains friendship data between users to support the in-game friend system and social features.