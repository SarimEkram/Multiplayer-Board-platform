# Use Case Descriptions

## Matchmaking

### Start Game Search

**Iteration:** 1

**Primary Actor**: Player

**Goal in Context**: Develop a feature for the user to join the matchmaking queue to join a match.

**Preconditions**: User has a player account and is authorized to play the game

**Trigger**: Player clicks the start game button

**Scenario**:
1. Player wants to play any one of the three games
2. Player clicks the start game button
3. The matchmaking system connects player to matchmaking system

**Post-Condition**: Player has entered matchmaking queue

**Exceptions**:
1. Player is blacklisted and cannot enter matchmaking queue.
2. Matchmaking queue is not entered due to network connection error.

**Priority**: High, required to play games

**When Available**: First development iteration

**Frequency of Use**: High

**Channel to actor**: Player UI that has the button

**Secondary Actor**: Player UI trigger

**Channel to Secondary Actor**: Game Screen

**Open Issues**:
1. Player cannot use screen until the matchmaking queue is not entered.


### Skilled Based Matchmaking

**Iteration:** 1

**Primary Actor**: System

**Goal in Context**: Match players based on their skill level for a game

**Preconditions**: Player is in the matchmaking queue

**Trigger**: Player enters matchmaking queue

**Scenario**:
1. Player has entered matchmaking queue
2. Matchmaking system finds similar skilled players in a game.
3. Matchmaking system signals players to be grouped.

**Post-Condition**: Player are grouped together

**Exceptions**:
1. Matchmaking groups different skilled players due to lack of users playing the game.
2. Network interrupt encountered.

**Priority**: High, required to play games

**When Available**: First development iteration

**Frequency of Use**: High

**Channel to actor**: Game Screen

**Secondary Actor**: Database

**Channel to Secondary Actor**: Game Screen

**Open Issues**:
1. What happens if one of the players decide to cancel matchmaking midway.
