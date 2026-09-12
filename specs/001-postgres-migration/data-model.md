# Phase 1 Data Model: CSV-to-PostgreSQL Persistence Migration

This schema is an internal implementation detail behind the unchanged `UserDatabase`,
`PlayerDatabase`, and `FriendDatabase` APIs (see [contracts/database-api-contract.md](./contracts/database-api-contract.md)).
Callers never see these tables directly.

## users

Maps to the current `userdata.csv` / `Authentication.User`.

| Column | Type | Notes |
|---|---|---|
| `user_id` | `BIGINT` | Primary key. Matches existing `int userID` (randomly generated 100000–999999 range, preserved via `UserDatabase.generateUniqueUserID()`). |
| `username` | `VARCHAR(255)` | No uniqueness constraint (see research.md §8 — matches existing behavior). Indexed for `getUserByUsername`. |
| `email` | `VARCHAR(255)` | No uniqueness constraint (matches existing behavior). Indexed for `getUserByEmail`. |
| `password_hash` | `VARCHAR(255)` | Stored value is whatever `User.getPassword()` already provides (existing hashing behavior, unchanged by this migration). |
| `win_ratio` | `DOUBLE PRECISION` | |
| `level` | `INTEGER` | |
| `online_status` | `BOOLEAN` | |

## players

Maps to the current `playerdata.csv` / `MatchmakingLeaderboard.Player` top-level fields.

| Column | Type | Notes |
|---|---|---|
| `user_id` | `BIGINT` | Primary key. Same value space as `users.user_id` (no `FOREIGN KEY` enforced, matching today's decoupled CSV files — `PlayerDatabase` operates independently of `UserDatabase` today). |
| `username` | `VARCHAR(255)` | Denormalized copy, matching the existing `Player` object's own `username` field (kept independent of `users.username` exactly as the two CSVs are independent today). |
| `level` | `INTEGER` | |

## player_game_stats

New normalized child table replacing the current CSV's repeated-per-`GameType` columns
(`{game}_wins, {game}_losses, {game}_mmr, {game}_winRatio, {game}_rank, {game}_gameSignal`).
One row per `(user_id, game_type)`, reconstructed into the same in-memory `Player` shape
(`Player.addWin/addLoss/setMMR/setWinRatio/setRank/setGameSignal`) that `loadPlayersFromCSV`
already builds today.

| Column | Type | Notes |
|---|---|---|
| `user_id` | `BIGINT` | Part of composite primary key `(user_id, game_type)`. |
| `game_type` | `VARCHAR(32)` | One of `TIC_TAC_TOE`, `CONNECT_FOUR`, `CHECKERS` (existing `GameType` enum values). |
| `wins` | `INTEGER` | |
| `losses` | `INTEGER` | |
| `mmr` | `INTEGER` | |
| `win_ratio` | `DOUBLE PRECISION` | |
| `rank_tier` | `VARCHAR(32)` | Existing `RankTier` enum value. |
| `game_signal` | `INTEGER` | |

## friendships

Maps to the current `friends.csv` / `Authentication.FriendDatabase`, which stores a mutual
relationship redundantly in both directions in memory (`friendsMap`). The table preserves that
same redundant-both-directions shape so `getFriends(userId)` remains a single, simple `SELECT`.

| Column | Type | Notes |
|---|---|---|
| `user_id` | `BIGINT` | Part of composite primary key `(user_id, friend_id)`. |
| `friend_id` | `BIGINT` | |

`addFriend(a, b)` inserts both `(a, b)` and `(b, a)`; `removeFriend(a, b)` deletes both rows —
identical to today's mutual add/remove behavior.

## game_history

New entity, not present in any current CSV file. Introduced per FR-013 to capture historical
gameplay data; **not surfaced in any UI** by this migration (per the spec's documented
Assumption). A row MUST be written here whenever a match completes — see
`GameProcessor` in contracts/database-api-contract.md for where this write is triggered.

| Column | Type | Notes |
|---|---|---|
| `id` | `BIGINT GENERATED ALWAYS AS IDENTITY` | Primary key. |
| `game_type` | `VARCHAR(32)` | |
| `player_one_id` | `BIGINT` | |
| `player_two_id` | `BIGINT` | |
| `winner_id` | `BIGINT` | Nullable (draws, e.g. Tic Tac Toe ties). |
| `played_at` | `TIMESTAMP` | |

## Relationships & integrity notes

- No `FOREIGN KEY` constraints are added between `users`, `players`, `friendships`, or
  `game_history` — the current CSV-based system has no such enforcement (the three files are
  read/written entirely independently), and adding FKs now would be a new integrity rule this
  migration is not meant to introduce (see research.md §8).
- All writes that touch more than one row (e.g., `saveAllToCSV`'s current full-rewrite behavior
  for `PlayerDatabase`/`UserDatabase`, or `addFriend`'s two-row mutual insert) are wrapped in a
  single database transaction, satisfying FR-003.
