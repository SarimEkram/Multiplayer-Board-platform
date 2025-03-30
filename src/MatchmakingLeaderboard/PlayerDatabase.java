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
                if (parts.length != 20) continue;

                int userID = Integer.parseInt(parts[0]);
                int level = Integer.parseInt(parts[1]);
                Player player = new Player(level, userID);

                for (int i = 0; i < 3; i++) {
                    player.setWinRatio(i + 1, Double.parseDouble(parts[2 + i]));
                    player.setMMR(Integer.parseInt(parts[5 + i]), i + 1);
                    player.setGameSignal(Integer.parseInt(parts[8 + i]), i + 1);

                    int wins = Integer.parseInt(parts[11 + i]);
                    int losses = Integer.parseInt(parts[14 + i]);

                    for (int w = 0; w < wins; w++) {
                        player.addWin(i + 1);
                    }
                    for (int l = 0; l < losses; l++) {
                        player.addLoss(i + 1);
                    }
                    int rankPoints = Integer.parseInt(parts[17 + i]);
                    player.setRank(new Rank(rankPoints), i + 1);
                }

                players.add(player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}