package MatchmakingLeaderboard;

import java.io.*;
import java.util.*;
//import java.util.stream.Collectors;

public class PlayerDatabase {
    private static final String FILE_PATH = "playerdata.csv";
    private static List<Player> players = new ArrayList<>();

    // Load all players when the class is first used
    static {
        loadPlayersFromCSV();
    }

    /**
     * Reads player data from the CSV file and loads it into memory.
     */
    public static void loadPlayersFromCSV() {
        players.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("userID")) continue;

                String[] parts = line.split(",");
                if (parts.length != 21) continue;

                int userID = Integer.parseInt(parts[0]);
                String username = parts[1];
                int level = Integer.parseInt(parts[2]);
                Player player = new Player(username, level, userID);

                GameType[] gameTypes = GameType.values();
                for (int i = 0; i < gameTypes.length; i++) {
                    GameType gameType = gameTypes[i];
                    int baseIndex = 3 + (i * 6);
                    player.setWinRatio(gameType, Double.parseDouble(parts[baseIndex]));
                    player.setMMR(Integer.parseInt(parts[baseIndex + 1]), gameType);
                    player.setGameSignal(Integer.parseInt(parts[baseIndex + 2]), gameType);

                    int wins = Integer.parseInt(parts[baseIndex + 3]);
                    int losses = Integer.parseInt(parts[baseIndex + 4]);

                    for (int w = 0; w < wins; w++) {
                        player.addWin(gameType);
                    }
                    for (int l = 0; l < losses; l++) {
                        player.addLoss(gameType);
                    }
                    int rankPoints = Integer.parseInt(parts[18 + i]);
                    player.setRank(new Rank(rankPoints), gameType);
                }

                players.add(player);

            }
        } catch (IOException e) {
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
        int index = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getUserID() == player.getUserID()) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            players.set(index, player);
        } else {
            players.add(player);
        }

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
     */
    private static boolean saveAllToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // CSV Header
            bw.write("userID,username,level,winRatio1,winRatio2,winRatio3,mmr1,mmr2,mmr3,signal1,signal2,signal3,wins1,wins2,wins3,losses1,losses2,losses3,rank1,rank2,rank3\n");


            for (Player p : players) {
                StringBuilder line = new StringBuilder();
                line.append(p.getUserID()).append(",")
                        .append(p.getUsername()).append(",")
                        .append(p.getLevel());

                for (GameType gameType : GameType.values()) {
                    line.append(",").append(p.getWinRatio(gameType))
                            .append(",").append(p.getMMR(gameType))
                            .append(",").append(p.getGameSignal(gameType))
                            .append(",").append(p.getWins(gameType))
                            .append(",").append(p.getLosses(gameType))
                            .append(",").append(p.getRank(gameType).getRankingPoints());
                }

                bw.write(line.toString() + "\n");

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