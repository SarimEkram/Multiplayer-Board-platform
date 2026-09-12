package Authentication;

import MatchmakingLeaderboard.PlayerDatabase;
import MatchmakingLeaderboard.persistence.DataSourceProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The UserDatabase class handles storing, loading, and managing users in the platform's
 * relational database (see specs/001-postgres-migration). Its public API is frozen —
 * see contracts/database-api-contract.md — so every method signature here matches the
 * original CSV-backed implementation exactly.
 */

public class UserDatabase {

    // Identity cache: the original CSV-backed implementation kept one long-lived User object per
    // account in an in-memory list, so repeated lookups of the same account returned the SAME
    // object reference. Callers (e.g. DeleteUserAccount.deletedUsers, a HashSet<User> that relies
    // on User's default reference-based equals/hashCode) depend on that. The database is still
    // the single source of truth for data; this cache only preserves object identity across reads.
    private static final Map<Integer, User> identityCache = new ConcurrentHashMap<>();

    // Static block: ensures the connection pool/schema are ready when this class is first used,
    // mirroring the original CSV version's eager load-on-class-access behavior.
    static {
        loadUsersFromCSV();
    }

    /**
     * Retained name for API compatibility (see contracts/database-api-contract.md). No in-memory
     * cache is kept any more — every read queries the database directly — so this simply ensures
     * the connection pool and schema migrations are initialized.
     */
    public static void loadUsersFromCSV() {
        DataSourceProvider.getDataSource();
    }

    /**
     * Saves or updates a user in the database.
     * If a row with the same ID already exists, it is updated.
     * If not, a new unique ID is assigned and a new row is inserted.
     * @param user The user to save
     * @return true if save is successful
     */
    public static boolean saveUser(User user) {
        return DataSourceProvider.withTransaction(connection -> {
            boolean exists;
            try (PreparedStatement check = connection.prepareStatement(
                    "SELECT 1 FROM users WHERE user_id = ?")) {
                check.setInt(1, user.getUserID());
                try (ResultSet rs = check.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (!exists) {
                user.setSuspendSave(true);
                user.setUserID(generateUniqueUserID());
                user.setSuspendSave(false);

                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO users (user_id, username, email, password_hash, win_ratio, level, online_status) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    bindUser(insert, user);
                    insert.executeUpdate();
                }
            } else {
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE users SET username = ?, email = ?, password_hash = ?, win_ratio = ?, "
                                + "level = ?, online_status = ? WHERE user_id = ?")) {
                    update.setString(1, user.getUsername());
                    update.setString(2, user.getEmail());
                    update.setString(3, user.getPassword());
                    update.setDouble(4, user.getWinRatio());
                    update.setInt(5, user.getLevel());
                    update.setBoolean(6, user.isOnline());
                    update.setInt(7, user.getUserID());
                    update.executeUpdate();
                }
            }
            // The saved object becomes the canonical cached instance for this ID from now on —
            // matches the original CSV implementation, which stored this same reference directly
            // into its in-memory list (see class-level note on identityCache).
            identityCache.put(user.getUserID(), user);
            return true;
        });
    }

    /**
     * Deletes a user by their ID.
     * @param userId The ID of the user to delete
     * @return true if user was deleted
     */
    public static boolean deleteUser(int userId) {
        // Matches the original CSV behavior: unconditionally succeeds (idempotent) regardless of
        // whether a matching row existed, rather than reporting whether a row was actually removed.
        DataSourceProvider.withConnection(connection -> {
            try (PreparedStatement delete = connection.prepareStatement(
                    "DELETE FROM users WHERE user_id = ?")) {
                delete.setInt(1, userId);
                delete.executeUpdate();
                return null;
            }
        });
        identityCache.remove(userId);
        PlayerDatabase.deletePlayer(userId);
        return true;
    }

    /**
     * Gets a user by their unique ID.
     * @param id user ID
     * @return User object or null if not found
     */
    public static User getUserById(int id) {
        return DataSourceProvider.withConnection(connection -> {
            try (PreparedStatement select = connection.prepareStatement(
                    "SELECT * FROM users WHERE user_id = ?")) {
                select.setInt(1, id);
                try (ResultSet rs = select.executeQuery()) {
                    return rs.next() ? cachedUserFrom(rs) : absentFromCache(id);
                }
            }
        });
    }

    /**
     * Gets a user by their email address.
     * @param email The user's email
     * @return User object or null if not found
     */
    public static User getUserByEmail(String email) {
        return DataSourceProvider.withConnection(connection -> {
            try (PreparedStatement select = connection.prepareStatement(
                    "SELECT * FROM users WHERE LOWER(email) = LOWER(?)")) {
                select.setString(1, email);
                try (ResultSet rs = select.executeQuery()) {
                    return rs.next() ? cachedUserFrom(rs) : null;
                }
            }
        });
    }

    /**
     * Gets a user by their username.
     * @param username user's username
     * @return User object or null if not found
     */
    public static User getUserByUsername(String username) {
        return DataSourceProvider.withConnection(connection -> {
            try (PreparedStatement select = connection.prepareStatement(
                    "SELECT * FROM users WHERE LOWER(username) = LOWER(?)")) {
                select.setString(1, username);
                try (ResultSet rs = select.executeQuery()) {
                    return rs.next() ? cachedUserFrom(rs) : null;
                }
            }
        });
    }

    public static int generateUniqueUserID() {
        int userID;
        do {
            userID = ThreadLocalRandom.current().nextInt(100000, 999999);
        }
        while (getUserById(userID) != null);
        return userID;
    }

    /**
     * Retained name for API compatibility (see contracts/database-api-contract.md). Clears all
     * persisted user data.
     * @return true if the operation completed successfully
     */
    public static boolean deleteCSVFile() {
        boolean result = DataSourceProvider.withConnection(connection -> {
            try (PreparedStatement delete = connection.prepareStatement("DELETE FROM users")) {
                delete.executeUpdate();
                return true;
            }
        });
        identityCache.clear();
        return result;
    }

    private static void bindUser(PreparedStatement statement, User user) throws java.sql.SQLException {
        statement.setInt(1, user.getUserID());
        statement.setString(2, user.getUsername());
        statement.setString(3, user.getEmail());
        statement.setString(4, user.getPassword());
        statement.setDouble(5, user.getWinRatio());
        statement.setInt(6, user.getLevel());
        statement.setBoolean(7, user.isOnline());
    }

    /**
     * Returns the single cached User instance for this row's user_id, creating it on first sight
     * and refreshing its fields in place on every subsequent read (never replacing the reference),
     * so repeated lookups of the same account are reference-equal — see {@link #identityCache}.
     */
    private static User cachedUserFrom(ResultSet rs) throws java.sql.SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password_hash");
        double winRatio = rs.getDouble("win_ratio");
        int level = rs.getInt("level");
        boolean onlineStatus = rs.getBoolean("online_status");

        User user = identityCache.computeIfAbsent(userId, id -> new User());
        user.setSuspendSave(true);
        user.setUserID(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setWinRatio(winRatio);
        user.setLevel(level);
        user.setOnlineStatus(onlineStatus);
        user.setSuspendSave(false);
        return user;
    }

    private static User absentFromCache(int userId) {
        identityCache.remove(userId);
        return null;
    }
}
