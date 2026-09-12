package Authentication;

import MatchmakingLeaderboard.persistence.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Manages a user's friend relationships in the platform's relational database (see
 * specs/001-postgres-migration). Its public API is frozen — see
 * contracts/database-api-contract.md — so every method signature here matches the original
 * CSV-backed implementation exactly. Friendships are mutual and stored redundantly in both
 * directions, exactly as the original in-memory/CSV representation did.
 */
public class FriendDatabase {

    static {
        loadFromCSV();
    }

    /**
     * Retained name for API compatibility (see contracts/database-api-contract.md). No in-memory
     * cache is kept any more — every read queries the database directly — so this simply ensures
     * the connection pool and schema migrations are initialized.
     */
    public static void loadFromCSV() {
        DataSourceProvider.getDataSource();
    }

    /**
     * Adds a friend for a user. This is a mutual friendship (both users become friends with each other).
     * @param userId ID of the first user
     * @param friendId ID of the friend to be added
     * @return true if friendship was added, false otherwise
     */
    public static boolean addFriend(int userId, int friendId) {
        if (userId == friendId) return false; // Cannot friend yourself

        return DataSourceProvider.withTransaction(connection -> {
            boolean added1 = insertFriendshipRow(connection, userId, friendId);
            boolean added2 = insertFriendshipRow(connection, friendId, userId);
            return added1 || added2;
        });
    }

    /**
     * Removes a friend from both users' friend lists (mutual removal).
     * @param userId ID of the user
     * @param friendId ID of the friend to be removed
     * @return true if the friend was removed from either list
     */
    public static boolean removeFriend(int userId, int friendId) {
        return DataSourceProvider.withTransaction(connection -> {
            boolean removed1 = deleteFriendshipRow(connection, userId, friendId);
            boolean removed2 = deleteFriendshipRow(connection, friendId, userId);
            return removed1 || removed2;
        });
    }

    /**
     * Retrieves the set of friend IDs for a given user.
     * @param userId ID of the user
     * @return a set of friend IDs, or an empty set if none exist
     */
    public static Set<Integer> getFriends(int userId) {
        return DataSourceProvider.withConnection(connection -> {
            Set<Integer> friends = new HashSet<>();
            try (PreparedStatement select = connection.prepareStatement(
                    "SELECT friend_id FROM friendships WHERE user_id = ?")) {
                select.setInt(1, userId);
                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next()) {
                        friends.add(rs.getInt("friend_id"));
                    }
                }
            }
            return friends;
        });
    }

    /**
     * Checks if two users are friends.
     * @param userId ID of the first user
     * @param friendId ID of the second user
     * @return true if they are friends, false otherwise
     */
    public static boolean areFriends(int userId, int friendId) {
        return DataSourceProvider.withConnection(connection -> {
            try (PreparedStatement select = connection.prepareStatement(
                    "SELECT 1 FROM friendships WHERE user_id = ? AND friend_id = ?")) {
                select.setInt(1, userId);
                select.setInt(2, friendId);
                try (ResultSet rs = select.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    /**
     * Retained name for API compatibility (see contracts/database-api-contract.md). Clears all
     * persisted friendship data. Also removes the legacy `friends.csv` file if one is still
     * present on disk (e.g. left over from before this migration, or as a migration-source
     * archive), so existing tests that check the file no longer exists keep passing.
     * @return true if the operation completed successfully
     */
    public static boolean deleteCSVFile() {
        boolean result = DataSourceProvider.withConnection(connection -> {
            try (PreparedStatement delete = connection.prepareStatement("DELETE FROM friendships")) {
                delete.executeUpdate();
                return true;
            }
        });
        new java.io.File("friends.csv").delete();
        return result;
    }

    private static boolean insertFriendshipRow(Connection connection, int userId, int friendId) throws SQLException {
        try (PreparedStatement check = connection.prepareStatement(
                "SELECT 1 FROM friendships WHERE user_id = ? AND friend_id = ?")) {
            check.setInt(1, userId);
            check.setInt(2, friendId);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    return false;
                }
            }
        }
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)")) {
            insert.setInt(1, userId);
            insert.setInt(2, friendId);
            insert.executeUpdate();
        }
        return true;
    }

    private static boolean deleteFriendshipRow(Connection connection, int userId, int friendId) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement(
                "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?")) {
            delete.setInt(1, userId);
            delete.setInt(2, friendId);
            return delete.executeUpdate() > 0;
        }
    }
}
