# Contract: `*Database` Public API (Frozen)

This is the project's real "interface contract" for this feature: the rest of the codebase
(business logic, controllers, and every existing test) depends on these exact static method
signatures. **None of these signatures may change.** Only the method bodies (internal
implementation) change, from CSV file I/O to SQL against PostgreSQL/H2.

Verified against the current source (`src/Authentication/UserDatabase.java`,
`src/Authentication/FriendDatabase.java`, `src/MatchmakingLeaderboard/PlayerDatabase.java`) as of
this plan.

## `Authentication.UserDatabase`

```java
public static void loadUsersFromCSV()                       // becomes: (re)load cache from DB
public static boolean saveUser(User user)
public static boolean deleteUser(int userId)
public static User getUserById(int id)
public static User getUserByEmail(String email)
public static User getUserByUsername(String username)
public static int generateUniqueUserID()
public static boolean deleteCSVFile()                        // becomes: clear all rows (test/reset use)
```

`loadUsersFromCSV()` and `deleteCSVFile()` keep their existing names (not renamed) even though
their internals no longer touch a CSV file, because they are called by name in other places in
the codebase; renaming them would itself be a breaking change to callers/tests. Behavior is
preserved: `loadUsersFromCSV()` (re)populates the in-memory/session view from the current source
of truth, and `deleteCSVFile()` clears all persisted user data.

## `MatchmakingLeaderboard.PlayerDatabase`

```java
public static void loadPlayersFromCSV()
public static boolean savePlayer(Player player)
public static Player getPlayerByUserID(int userID)
public static boolean deletePlayer(int userID)
public static List<Player> getAllPlayers()
public static Player getPlayerByUsername(String username)
```

## `Authentication.FriendDatabase`

```java
public static void loadFromCSV()
public static boolean addFriend(int userId, int friendId)
public static boolean removeFriend(int userId, int friendId)
public static Set<Integer> getFriends(int userId)
public static boolean areFriends(int userId, int friendId)
public static boolean deleteCSVFile()
```

## Behavioral guarantees (unchanged from current implementation)

- All methods remain callable without any setup by the caller (today, a `static { ... }`
  initializer loads data on class-first-use; the PostgreSQL-backed version keeps this — the
  initializer instead opens the pooled connection and ensures migrations have run).
- Return value semantics (`true`/`false` for success, `null` for not-found, empty collections for
  no-results) are unchanged.
- No method signature gains new required parameters or a checked exception; any new failure mode
  (e.g., unreachable database) is handled internally per FR-004 (retry/clear error state) rather
  than surfaced as a new exception type callers must now catch.

## New, additive-only surface (not required by existing callers)

- `MatchmakingLeaderboard.persistence.DataSourceProvider` — internal, package-private/public
  utility for obtaining the pooled `DataSource`; not called by any existing business/controller
  code.
- `MatchmakingLeaderboard.persistence.MigrationRunner` — invoked once at application startup
  (`MainApplication`) to apply Flyway migrations; not part of the `*Database` classes' contract.
- `ca.ucalgary.groupprojectgui.p3.tools.CsvToPostgresMigrator` — standalone, manually-invoked
  one-time migration CLI; not part of the running application's normal call path.
- `MatchmakingLeaderboard.PlayerDatabase.recordGameHistory(GameType gameType, int playerOneId, int playerTwoId, Integer winnerId)`
  — new method, additive only. Per FR-013, `MatchmakingLeaderboard.GameProcessor` MUST call this
  once per completed match (alongside its existing `PlayerDatabase.savePlayer(...)` calls) to
  populate `game_history`. No existing caller is required to change; this is a new call added
  inside `GameProcessor` only.

## Concurrency guarantee (FR-003 / spec Edge Cases)

`PlayerDatabase.savePlayer(Player player)` performs a read-modify-write of a player's full stats
row (the in-memory `Player` object is built from a prior read, mutated by `GameProcessor`, then
saved as a whole). Wrapping the write alone in a transaction does **not** prevent a lost update
between two concurrent `savePlayer` calls for the same `user_id`. The implementation MUST protect
the read-modify-write with row-level locking (`SELECT ... FOR UPDATE` on the `players`/
`player_game_stats` rows for that `user_id`, held for the duration of the same transaction that
performs the subsequent write) so concurrent game completions for the same player serialize
correctly instead of silently dropping one result.
