# Manual GUI Test Case Document

**Project:** OMG
**Team:** GUI
**Purpose:** Ensure that all GUI features function correctly after each update.

## General Testing Instructions

- Launch the application and verify successful startup.
- Confirm all screens load without error.
- Follow test cases in order during major updates or before demos.

## Home Page & Navigation

| Test Case ID | Scenario                | Steps                                | Expected Outcome                                           |
|--------------|-------------------------|--------------------------------------|------------------------------------------------------------|
| GUI-HOME-01  | Load Home Page          | Launch app                           | Home screen loads with navigation buttons                  |
| GUI-HOME-02  | Navigate to Game        | Click on “Play” / "Start Game"       | Game mode selection popup appears                          |
| GUI-HOME-03  | Navigate to Leaderboard | Click “Leaderboard”                  | Leaderboard screen loads properly                          |
| GUI-HOME-04  | Log Out                 | Click “Logout” button                | Logout confirmation popup appears                          |
| GUI-HOME-05  | Remove Friend           | Click dot and select "remove friend" | Confirmation popup appears and friend is removed from list |
| GUI-HOME-06  | Add Friend              | Click add button and select friend   | Friend is added to list                                    |
|              |                         |                                      |                                                            |


## Game Screens (TicTacToe, Connect4 & Checkers)

| Test Case ID | Scenario          | Steps                      | Expected Outcome                                                       |
|--------------|-------------------|----------------------------|------------------------------------------------------------------------|
| GUI-GAME-01  | Start TicTacToe   | From home, start TicTacToe | Game board renders correctly                                           |
| GUI-GAME-02  | Player Move       | Click a grid space         | Symbol (X/O) appears and disables cell                                 |
| GUI-GAME-03  | Game Over Display | Complete a winning game    | “Game Over” popup appears with correct winner and the button is active |
| GUI-GAME-04  | Leave Game        | Click "Leave" during game  | Leave confirmation popup is shown and button are active                |
| GUI-GAME-05  | Start Checkers    | From home, start Checkers  | Checkerboard loads with proper initial pieces                          |
| GUI-GAME-06  | Start Chat        | Send messages to opponent  | The message is displayed from the player with current turn.            |

## Networked Features (When Implemented)

| Test Case ID | Scenario | Steps | Expected Outcome |
|--------------|----------|-------|------------------|
| GUI-NET-01 | Online Friends Popup | Click on “Online Friends” | Friends list popup appears |
| GUI-NET-02 | Invite Friend | Select a friend and click invite | Invite confirmation appears |
| GUI-NET-03 | Networked Game Start | Accept an invite | Game starts with both players in sync |
| GUI-NET-04 | Disconnect | One player leaves | Opponent sees a disconnect popup |

## Popups and Dialogs

| Test Case ID | Scenario            | Steps                  | Expected Outcome                                                |
|--------------|---------------------|------------------------|-----------------------------------------------------------------|
| GUI-POP-01   | Logout Confirmation | Click “Logout”         | Confirmation popup appears with Yes/No                          |
| GUI-POP-02   | Opponent Selection  | Start a networked game | Opponent selection popup appears                                |
| GUI-POP-03   | Friend Selection    | Start a friendly game  | Friend selection popup appears with list of active players only |
| GUI-POP-04   | Invalid Input       | Attempt invalid move   | Appropriate error shown or move rejected                        |


