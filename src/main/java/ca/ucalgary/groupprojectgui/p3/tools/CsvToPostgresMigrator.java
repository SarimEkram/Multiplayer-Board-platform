package ca.ucalgary.groupprojectgui.p3.tools;

import MatchmakingLeaderboard.persistence.DataSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * One-time migration of the platform's original CSV files (userdata.csv, playerdata.csv,
 * friends.csv) into the PostgreSQL database, per spec FR-005 (specs/001-postgres-migration).
 *
 * <p>This writes directly via SQL rather than through {@code UserDatabase.saveUser} /
 * {@code PlayerDatabase.savePlayer}: those methods assign a brand-new random ID to any user_id
 * they don't already recognize (the correct behavior for new accounts created by the running
 * application), which would discard the original IDs that playerdata.csv and friends.csv
 * cross-reference. Migration must preserve the original IDs exactly, so it upserts rows directly
 * — insert if the ID is new, update in place if the migration is re-run (idempotent, FR-005 edge
 * case).</p>
 *
 * <p>Malformed or orphaned rows are logged and skipped rather than aborting the whole run.</p>
 */
public class CsvToPostgresMigrator {

    private static final Logger LOG = LoggerFactory.getLogger(CsvToPostgresMigrator.class);

    public static void main(String[] args) {
        Path root = Path.of("");
        MigrationResult result = migrate(
                root.resolve("userdata.csv"),
                root.resolve("playerdata.csv"),
                root.resolve("friends.csv"));
        LOG.info("Migration complete: {}", result);
    }

    public static MigrationResult migrate(Path userDataCsv, Path playerDataCsv, Path friendsCsv) {
        int usersMigrated = migrateUsers(userDataCsv);
        int playersMigrated = migratePlayers(playerDataCsv);
        int friendshipsMigrated = migrateFriendships(friendsCsv);
        return new MigrationResult(usersMigrated, playersMigrated, friendshipsMigrated);
    }

    private static int migrateUsers(Path csvPath) {
        List<String> lines = readLines(csvPath);
        if (lines.isEmpty()) {
            return 0;
        }

        int migrated = 0;
        for (String line : lines.subList(1, lines.size())) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split(",", -1);
            if (parts.length != 7) {
                LOG.warn("Skipping malformed userdata.csv row (expected 7 columns, got {}): {}", parts.length, line);
                continue;
            }
            try {
                int userId = Integer.parseInt(parts[0].trim());
                String username = parts[1];
                String email = parts[2];
                String passwordHash = parts[3];
                double winRatio = Double.parseDouble(parts[4]);
                int level = Integer.parseInt(parts[5]);
                boolean onlineStatus = Boolean.parseBoolean(parts[6]);

                upsertUser(userId, username, email, passwordHash, winRatio, level, onlineStatus);
                migrated++;
            } catch (RuntimeException e) {
                LOG.warn("Skipping malformed userdata.csv row: {} ({})", line, e.getMessage());
            }
        }
        return migrated;
    }

    private static int migratePlayers(Path csvPath) {
        List<String> lines = readLines(csvPath);
        if (lines.isEmpty()) {
            return 0;
        }

        int migrated = 0;
        for (String line : lines.subList(1, lines.size())) {
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split(",", -1);
            if (parts.length < 21) {
                LOG.warn("Skipping malformed playerdata.csv row (expected 21 columns, got {}): {}", parts.length, line);
                continue;
            }
            try {
                String username = parts[0];
                int level = Integer.parseInt(parts[1]);
                int userId = Integer.parseInt(parts[2]);

                upsertPlayer(userId, username, level);

                int index = 3;
                for (MatchmakingLeaderboard.GameType gameType : MatchmakingLeaderboard.GameType.values()) {
                    int wins = Integer.parseInt(parts[index++]);
                    int losses = Integer.parseInt(parts[index++]);
                    int mmr = Integer.parseInt(parts[index++]);
                    double winRatio = Double.parseDouble(parts[index++]);
                    String rankTier = parts[index++];
                    int gameSignal = Integer.parseInt(parts[index++]);

                    upsertPlayerGameStats(userId, gameType.name(), wins, losses, mmr, winRatio, rankTier, gameSignal);
                }
                migrated++;
            } catch (RuntimeException e) {
                LOG.warn("Skipping malformed playerdata.csv row: {} ({})", line, e.getMessage());
            }
        }
        return migrated;
    }

    private static int migrateFriendships(Path csvPath) {
        List<String> lines = readLines(csvPath);
        if (lines.isEmpty()) {
            return 0;
        }

        int migrated = 0;
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            String[] sides = line.split(":", 2);
            if (sides.length < 1) {
                LOG.warn("Skipping malformed friends.csv row: {}", line);
                continue;
            }
            try {
                int userId = Integer.parseInt(sides[0].trim());
                if (!userExists(userId)) {
                    LOG.warn("Skipping friends.csv row referencing unknown user_id {} (orphaned reference): {}", userId, line);
                    continue;
                }
                if (sides.length < 2 || sides[1].isBlank()) {
                    continue;
                }
                for (String friendIdStr : sides[1].split(",")) {
                    if (friendIdStr.isBlank()) {
                        continue;
                    }
                    int friendId = Integer.parseInt(friendIdStr.trim());
                    if (!userExists(friendId)) {
                        LOG.warn("Skipping friends.csv edge referencing unknown user_id {} (orphaned reference): {}:{}", friendId, userId, friendId);
                        continue;
                    }
                    upsertFriendship(userId, friendId);
                    migrated++;
                }
            } catch (RuntimeException e) {
                LOG.warn("Skipping malformed friends.csv row: {} ({})", line, e.getMessage());
            }
        }
        return migrated;
    }

    private static List<String> readLines(Path csvPath) {
        if (!Files.exists(csvPath)) {
            LOG.info("{} does not exist; nothing to migrate for this file", csvPath);
            return List.of();
        }
        try {
            return Files.readAllLines(csvPath);
        } catch (IOException e) {
            LOG.error("Failed to read {}", csvPath, e);
            return List.of();
        }
    }

    private static boolean userExists(int userId) {
        return DataSourceProvider.withConnection(connection -> {
            try (PreparedStatement check = connection.prepareStatement("SELECT 1 FROM users WHERE user_id = ?")) {
                check.setInt(1, userId);
                try (ResultSet rs = check.executeQuery()) {
                    return rs.next();
                }
            }
        });
    }

    private static void upsertUser(int userId, String username, String email, String passwordHash,
                                    double winRatio, int level, boolean onlineStatus) {
        DataSourceProvider.withTransaction(connection -> {
            if (rowExists(connection, "users", userId)) {
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE users SET username = ?, email = ?, password_hash = ?, win_ratio = ?, "
                                + "level = ?, online_status = ? WHERE user_id = ?")) {
                    update.setString(1, username);
                    update.setString(2, email);
                    update.setString(3, passwordHash);
                    update.setDouble(4, winRatio);
                    update.setInt(5, level);
                    update.setBoolean(6, onlineStatus);
                    update.setInt(7, userId);
                    update.executeUpdate();
                }
            } else {
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO users (user_id, username, email, password_hash, win_ratio, level, online_status) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    insert.setInt(1, userId);
                    insert.setString(2, username);
                    insert.setString(3, email);
                    insert.setString(4, passwordHash);
                    insert.setDouble(5, winRatio);
                    insert.setInt(6, level);
                    insert.setBoolean(7, onlineStatus);
                    insert.executeUpdate();
                }
            }
            return null;
        });
    }

    private static void upsertPlayer(int userId, String username, int level) {
        DataSourceProvider.withTransaction(connection -> {
            if (rowExists(connection, "players", userId)) {
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE players SET username = ?, level = ? WHERE user_id = ?")) {
                    update.setString(1, username);
                    update.setInt(2, level);
                    update.setInt(3, userId);
                    update.executeUpdate();
                }
            } else {
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO players (user_id, username, level) VALUES (?, ?, ?)")) {
                    insert.setInt(1, userId);
                    insert.setString(2, username);
                    insert.setInt(3, level);
                    insert.executeUpdate();
                }
            }
            return null;
        });
    }

    private static void upsertPlayerGameStats(int userId, String gameType, int wins, int losses, int mmr,
                                               double winRatio, String rankTier, int gameSignal) {
        DataSourceProvider.withTransaction(connection -> {
            boolean exists;
            try (PreparedStatement check = connection.prepareStatement(
                    "SELECT 1 FROM player_game_stats WHERE user_id = ? AND game_type = ?")) {
                check.setInt(1, userId);
                check.setString(2, gameType);
                try (ResultSet rs = check.executeQuery()) {
                    exists = rs.next();
                }
            }
            if (exists) {
                try (PreparedStatement update = connection.prepareStatement(
                        "UPDATE player_game_stats SET wins = ?, losses = ?, mmr = ?, win_ratio = ?, "
                                + "rank_tier = ?, game_signal = ? WHERE user_id = ? AND game_type = ?")) {
                    update.setInt(1, wins);
                    update.setInt(2, losses);
                    update.setInt(3, mmr);
                    update.setDouble(4, winRatio);
                    update.setString(5, rankTier);
                    update.setInt(6, gameSignal);
                    update.setInt(7, userId);
                    update.setString(8, gameType);
                    update.executeUpdate();
                }
            } else {
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO player_game_stats (user_id, game_type, wins, losses, mmr, win_ratio, rank_tier, game_signal) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                    insert.setInt(1, userId);
                    insert.setString(2, gameType);
                    insert.setInt(3, wins);
                    insert.setInt(4, losses);
                    insert.setInt(5, mmr);
                    insert.setDouble(6, winRatio);
                    insert.setString(7, rankTier);
                    insert.setInt(8, gameSignal);
                    insert.executeUpdate();
                }
            }
            return null;
        });
    }

    private static void upsertFriendship(int userId, int friendId) {
        DataSourceProvider.withTransaction(connection -> {
            try (PreparedStatement check = connection.prepareStatement(
                    "SELECT 1 FROM friendships WHERE user_id = ? AND friend_id = ?")) {
                check.setInt(1, userId);
                check.setInt(2, friendId);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        return null;
                    }
                }
            }
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)")) {
                insert.setInt(1, userId);
                insert.setInt(2, friendId);
                insert.executeUpdate();
            }
            return null;
        });
    }

    private static boolean rowExists(Connection connection, String table, int userId) throws SQLException {
        try (PreparedStatement check = connection.prepareStatement(
                "SELECT 1 FROM " + table + " WHERE user_id = ?")) {
            check.setInt(1, userId);
            try (ResultSet rs = check.executeQuery()) {
                return rs.next();
            }
        }
    }

    public record MigrationResult(int usersMigrated, int playersMigrated, int friendshipsMigrated) {
    }
}
