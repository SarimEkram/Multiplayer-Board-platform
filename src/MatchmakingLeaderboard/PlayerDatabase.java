package MatchmakingLeaderboard;

import MatchmakingLeaderboard.persistence.DataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * PlayerDatabase stores and retrieves player stats/leaderboard data in the platform's relational
 * database (see specs/001-postgres-migration). Its public API is frozen — see
 * contracts/database-api-contract.md — so every method signature here matches the original
 * CSV-backed implementation exactly.
 */
public class PlayerDatabase {

    // Ensures the connection pool/schema are ready when this class is first used, mirroring the
    // original CSV version's eager load-on-class-access behavior.
    static {
        loadPlayersFromCSV();
    }

    /**
     * Retained name for API compatibility (see contracts/database-api-contract.md). No in-memory
     * cache is kept any more — every read queries the database directly — so this simply ensures
     * the connection pool and schema migrations are initialized.
     */
    public static void loadPlayersFromCSV() {
        DataSourceProvider.getDataSource();
    }

    /**
     * Saves or updates a player's core info and all per-game stats.
     *
     * @param player the player to save
     * @return true if successful
     */
    public static boolean savePlayer(Player player) {
        return DataSourceProvider.withTransaction(connection -> {
            // Row-level lock on this player's existing stat rows (if any) before writing, so two
            // concurrent savePlayer calls for the same player serialize instead of interleaving
            // mid-write across the players/player_game_stats tables (FR-003; see
            // contracts/database-api-contract.md "Concurrency guarantee"). This matches — rather
            // than changes — the original CSV implementation's last-writer-wins semantics: it
            // only guarantees the write itself is atomic, not that two independent in-memory
            // mutations of the same player get merged.
            lockPlayerRows(connection, player.getUserID());

            upsertPlayer(connection, player);
            for (GameType gameType : GameType.values()) {
                upsertPlayerGameStats(connection, player, gameType);
            }
            return true;
        });
    }

    /**
     * Retrieves a player by their user ID.
     */
    public static Player getPlayerByUserID(int userID) {
        return DataSourceProvider.withConnection(connection -> {
            try (PreparedStatement select = connection.prepareStatement(
                    "SELECT * FROM players WHERE user_id = ?")) {
                select.setInt(1, userID);
                try (ResultSet rs = select.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return loadFullPlayer(connection, rs);
                }
            }
        });
    }

    /**
     * Deletes a player by user ID.
     */
    public static boolean deletePlayer(int userID) {
        return DataSourceProvider.withTransaction(connection -> {
            try (PreparedStatement deleteStats = connection.prepareStatement(
                    "DELETE FROM player_game_stats WHERE user_id = ?")) {
                deleteStats.setInt(1, userID);
                deleteStats.executeUpdate();
            }
            try (PreparedStatement deletePlayer = connection.prepareStatement(
                    "DELETE FROM players WHERE user_id = ?")) {
                deletePlayer.setInt(1, userID);
                deletePlayer.executeUpdate();
            }
            return true;
        });
    }

    public static List<Player> getAllPlayers() {
        return DataSourceProvider.withConnection(connection -> {
            List<Player> players = new ArrayList<>();
            try (PreparedStatement select = connection.prepareStatement("SELECT * FROM players");
                 ResultSet rs = select.executeQuery()) {
                while (rs.next()) {
                    players.add(loadFullPlayer(connection, rs));
                }
            }
            return players;
        });
    }

    /**
     * Retrieves a player by their username.
     * @param username The username of the player
     * @return Player object or null if not found
     */
    public static Player getPlayerByUsername(String username) {
        return DataSourceProvider.withConnection(connection -> {
            try (PreparedStatement select = connection.prepareStatement(
                    "SELECT * FROM players WHERE LOWER(username) = LOWER(?)")) {
                select.setString(1, username);
                try (ResultSet rs = select.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    return loadFullPlayer(connection, rs);
                }
            }
        });
    }

    /**
     * Records a completed match in game_history. Additive-only method, not part of the frozen
     * `*Database` API contract — called from GameProcessor per FR-013.
     */
    public static void recordGameHistory(GameType gameType, int playerOneId, int playerTwoId, Integer winnerId) {
        DataSourceProvider.withConnection(connection -> {
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO game_history (game_type, player_one_id, player_two_id, winner_id, played_at) "
                            + "VALUES (?, ?, ?, ?, ?)")) {
                insert.setString(1, gameType.name());
                insert.setInt(2, playerOneId);
                insert.setInt(3, playerTwoId);
                if (winnerId != null) {
                    insert.setInt(4, winnerId);
                } else {
                    insert.setNull(4, java.sql.Types.BIGINT);
                }
                insert.setTimestamp(5, Timestamp.from(Instant.now()));
                insert.executeUpdate();
                return null;
            }
        });
    }

    private static void lockPlayerRows(Connection connection, int userId) throws SQLException {
        try (PreparedStatement lock = connection.prepareStatement(
                "SELECT user_id FROM player_game_stats WHERE user_id = ? FOR UPDATE")) {
            lock.setInt(1, userId);
            lock.executeQuery().close();
        }
    }

    private static void upsertPlayer(Connection connection, Player player) throws SQLException {
        boolean exists;
        try (PreparedStatement check = connection.prepareStatement(
                "SELECT 1 FROM players WHERE user_id = ?")) {
            check.setInt(1, player.getUserID());
            try (ResultSet rs = check.executeQuery()) {
                exists = rs.next();
            }
        }

        if (exists) {
            try (PreparedStatement update = connection.prepareStatement(
                    "UPDATE players SET username = ?, level = ? WHERE user_id = ?")) {
                update.setString(1, player.getUsername());
                update.setInt(2, player.getLevel());
                update.setInt(3, player.getUserID());
                update.executeUpdate();
            }
        } else {
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO players (user_id, username, level) VALUES (?, ?, ?)")) {
                insert.setInt(1, player.getUserID());
                insert.setString(2, player.getUsername());
                insert.setInt(3, player.getLevel());
                insert.executeUpdate();
            }
        }
    }

    private static void upsertPlayerGameStats(Connection connection, Player player, GameType gameType) throws SQLException {
        boolean exists;
        try (PreparedStatement check = connection.prepareStatement(
                "SELECT 1 FROM player_game_stats WHERE user_id = ? AND game_type = ?")) {
            check.setInt(1, player.getUserID());
            check.setString(2, gameType.name());
            try (ResultSet rs = check.executeQuery()) {
                exists = rs.next();
            }
        }

        String rankTier = player.rankForPlayer(gameType);

        if (exists) {
            try (PreparedStatement update = connection.prepareStatement(
                    "UPDATE player_game_stats SET wins = ?, losses = ?, mmr = ?, win_ratio = ?, "
                            + "rank_tier = ?, game_signal = ? WHERE user_id = ? AND game_type = ?")) {
                update.setInt(1, player.getWins(gameType));
                update.setInt(2, player.getLosses(gameType));
                update.setInt(3, player.getMMR(gameType));
                update.setDouble(4, player.getWinRatio(gameType));
                update.setString(5, rankTier);
                update.setInt(6, player.getGameSignal(gameType));
                update.setInt(7, player.getUserID());
                update.setString(8, gameType.name());
                update.executeUpdate();
            }
        } else {
            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO player_game_stats (user_id, game_type, wins, losses, mmr, win_ratio, rank_tier, game_signal) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                insert.setInt(1, player.getUserID());
                insert.setString(2, gameType.name());
                insert.setInt(3, player.getWins(gameType));
                insert.setInt(4, player.getLosses(gameType));
                insert.setInt(5, player.getMMR(gameType));
                insert.setDouble(6, player.getWinRatio(gameType));
                insert.setString(7, rankTier);
                insert.setInt(8, player.getGameSignal(gameType));
                insert.executeUpdate();
            }
        }
    }

    /** Loads a Player's core row plus all of its per-game stats rows. */
    private static Player loadFullPlayer(Connection connection, ResultSet playerRow) throws SQLException {
        String username = playerRow.getString("username");
        int level = playerRow.getInt("level");
        int userID = playerRow.getInt("user_id");

        Player player = new Player(username, level, userID);

        try (PreparedStatement select = connection.prepareStatement(
                "SELECT * FROM player_game_stats WHERE user_id = ?")) {
            select.setInt(1, userID);
            try (ResultSet rs = select.executeQuery()) {
                while (rs.next()) {
                    GameType gameType = GameType.valueOf(rs.getString("game_type"));

                    int wins = rs.getInt("wins");
                    int losses = rs.getInt("losses");
                    for (int w = 0; w < wins; w++) {
                        player.addWin(gameType);
                    }
                    for (int l = 0; l < losses; l++) {
                        player.addLoss(gameType);
                    }
                    player.setMMR(rs.getInt("mmr"), gameType);
                    player.setWinRatio(gameType, rs.getDouble("win_ratio"));
                    RankTier tier = RankTier.valueOf(rs.getString("rank_tier"));
                    player.setRank(new Rank(tier), gameType);
                    player.setGameSignal(rs.getInt("game_signal"), gameType);
                }
            }
        }

        return player;
    }
}
