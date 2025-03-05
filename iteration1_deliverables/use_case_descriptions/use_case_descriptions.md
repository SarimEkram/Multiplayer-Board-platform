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


### Add player Groups

**Iteration:** 1

**Primary Actor**: System

**Goal in Context**: Group matched players with their selected game

**Preconditions**: Players are grouped by skill based matchmaking system

**Trigger**: Player being grouped together

**Scenario**:
1. Matchmaking system has grouped players together based on skill.
2. Matchmaking system sends grouped players to match their game.
3. Groups are matched to their game worlds.

**Post-Condition**: Groups attached with their game world

**Exceptions**:
1. No game worlds are available for players to enter.
2. Network interrupt encountered.

**Priority**: High, required to play games

**When Available**: First development iteration

**Frequency of Use**: High

**Channel to actor**: Matchmaking system

**Secondary Actor**: Players

**Channel to Secondary Actor**: Game Screen

**Open Issues**:
1. Players cannot cancel matchmaking after reaching this level. They cannot quit the game midway.


### Signal Start Game

**Iteration:** 1

**Primary Actor**: System

**Goal in Context**: Signal the game system to start the game world simulation.

**Preconditions**: Player groups are matched with a game world

**Trigger**: Groups matched with a game world.

**Scenario**:
1. Player groups are matched with their game world.
2. The subsystem checks if game world is ready.
3. Signal sent to game system to start game world simulation.

**Post-Condition**: Game simulation starts

**Exceptions**:
1. Network interrupt causes game to be dropped.
2. The game system is operating at max capacity.

**Priority**: High, required to play games

**When Available**: First development iteration

**Frequency of Use**: High

**Channel to actor**: Matchmaking system

**Secondary Actor**: Players

**Channel to Secondary Actor**: Game Screen

**Open Issues**:
1. Player screen is not operational until the game simulation is loaded.


### Spectate Game

**Iteration:** 3

**Primary Actor**: System

**Goal in Context**: Signal database to join spectator player to join the game world

**Preconditions**: The spectator is not blacklisted and game world is running

**Trigger**: Spectator presses the spectate button

**Scenario**:
1. Spectator wants to view their friend's gameplay.
2. The system checks if the game world is active and spectator can join.
3. Player joins the game world simulation as spectator to their friend.

**Post-Condition**: Spectator can spectate the game in real time.

**Exceptions**:
1. Player is not authorized to spectate the game
2. Network issues disallow spectating.

**Priority**: Medium, required for increased player satisfaction.

**When Available**: third development iteration

**Frequency of Use**: Fair

**Channel to actor**: Matchmaking system

**Secondary Actor**: Players

**Channel to Secondary Actor**: Game Screen

**Open Issues**:
1. What happens when the game world is operating at max capacity and cannot host more players?
