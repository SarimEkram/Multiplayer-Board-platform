package MatchmakingLeaderboard;

import java.io.*;
import java.util.*;

public class PlayerDatabase {
    private static final String FILE_PATH = "playerdata.csv";
    private static List<Player> players = new ArrayList<>();

    // Load all players when the class is first used
    static {
        loadPlayersFromCSV();
    }

    /**
     * Reads player data from the CSV file and loads it into memory.
     * Expected CSV columns:
     * username,level,userID,
     * For each GameType (TIC_TAC_TOE, CONNECT_FOUR, CHECKERS):
     * wins,losses,mmr,winRatio,rankingPoints,gameSignal
     */
    public static void loadPlayersFromCSV() {
        players.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",");
                // For 3 game types, we expect 3 + (6 * 3) = 21 columns
                if (parts.length < 21) continue;

                String username = parts[0];
                int level = Integer.parseInt(parts[1]);
                int userID = Integer.parseInt(parts[2]);

                Player player = new Player(username, level, userID);
                int index = 3;
                for (GameType gameType : GameType.values()) {
                    int wins = Integer.parseInt(parts[index++]);
                    int losses = Integer.parseInt(parts[index++]);
                    int mmr = Integer.parseInt(parts[index++]);
                    double winRatio = Double.parseDouble(parts[index++]);
                    RankTier tier = RankTier.valueOf(parts[index++]);
                    int gameSignal = Integer.parseInt(parts[index++]);

                    // Add wins and losses (which will update the win ratio)
                    for (int w = 0; w < wins; w++) {
                        player.addWin(gameType);
                    }
                    for (int l = 0; l < losses; l++) {
                        player.addLoss(gameType);
                    }
                    // Set MMR and override win ratio to the saved value
                    player.setMMR(mmr, gameType);
                    player.setWinRatio(gameType, winRatio);
                    // Set rank using the rankingPoints (the Rank constructor updates the tier)
                    player.setRank(new Rank(tier), gameType);
                    // Set game signal
                    player.setGameSignal(gameSignal, gameType);
                }
                players.add(player);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves or updates a player in memory and writes all players to the CSV.
     *
     * @param player the player to save
     * @return true if successful
     */
    public static boolean savePlayer(Player player) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getUserID() == player.getUserID()) {
                players.set(i, player);
                return saveAllToCSV();
            }
        }
        players.add(player);
        return saveAllToCSV();
    }

    /**
     * Retrieves a player by their user ID.
     */
    public static Player getPlayerByUserID(int userID) {
        return players.stream()
                .filter(p -> p.getUserID() == userID)
                .findFirst()
                .orElse(null);
    }

    /**
     * Deletes a player by user ID.
     */
    public static boolean deletePlayer(int userID) {
        players.removeIf(p -> p.getUserID() == userID);
        return saveAllToCSV();
    }

    /**
     * Writes all player data back to the CSV file.
     * CSV Format:
     * username,level,userID,
     * For each GameType: wins,losses,mmr,winRatio,rankingPoints,gameSignal
     */
    private static boolean saveAllToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // Write header
            bw.write("username,level,userID");
            for (GameType game : GameType.values()) {
                bw.write("," + game + "_wins," + game + "_losses," + game + "_mmr,"
                        + game + "_winRatio," + game + "_rank," + game + "_gameSignal");
            }
            bw.newLine();

            // Write player data
            for (Player p : players) {
                StringBuilder line = new StringBuilder();
                line.append(p.getUsername()).append(",")
                        .append(p.getLevel()).append(",")
                        .append(p.getUserID());
                for (GameType game : GameType.values()) {
                    line.append(",").append(p.getWins(game))
                            .append(",").append(p.getLosses(game))
                            .append(",").append(p.getMMR(game))
                            .append(",").append(p.getWinRatio(game))
                            .append(",").append(p.getRank(game).getCurrentTier())
                            .append(",").append(p.getGameSignal(game));
                }
                bw.write(line.toString());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Player> getAllPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * Retrieves a player by their username.
     * @param username The username of the player
     * @return Player object or null if not found
     */
    public static Player getPlayerByUsername(String username) {
        return players.stream()
                .filter(p -> p.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }
}
