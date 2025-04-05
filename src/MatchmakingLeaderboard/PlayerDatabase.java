package MatchmakingLeaderboard;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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

                for (int i = 0; i < 3; i++) {
                    player.setWinRatio(i + 1, Double.parseDouble(parts[2 + i]));
                    player.setMMR(Integer.parseInt(parts[6 + i]), i + 1);
                    player.setGameSignal(Integer.parseInt(parts[9 + i]), i + 1);

                    int wins = Integer.parseInt(parts[12 + i]);
                    int losses = Integer.parseInt(parts[15 + i]);

                    for (int w = 0; w < wins; w++) {
                        player.addWin(i + 1);
                    }
                    for (int l = 0; l < losses; l++) {
                        player.addLoss(i + 1);
                    }
                    int rankPoints = Integer.parseInt(parts[18 + i]);
                    player.setRank(new Rank(rankPoints), i + 1);
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
                bw.write(p.getUserID() + "," +
                        p.getUsername() + "," +
                        p.getLevel() + "," +
                        p.getWinRatio(1) + "," +
                        p.getWinRatio(2) + "," +
                        p.getWinRatio(3) + "," +
                        p.getMMR(1) + "," +
                        p.getMMR(2) + "," +
                        p.getMMR(3) + "," +
                        p.getGameSignal(1) + "," +
                        p.getGameSignal(2) + "," +
                        p.getGameSignal(3) + "," +
                        p.getWins(1) + "," +
                        p.getWins(2) + "," +
                        p.getWins(3) + "," +
                        p.getLosses(1) + "," +
                        p.getLosses(2) + "," +
                        p.getLosses(3) + "," +
                        p.getRank(1).getRankingPoints() + "," +
                        p.getRank(2).getRankingPoints() + "," +
                        p.getRank(3).getRankingPoints() + "\n");
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