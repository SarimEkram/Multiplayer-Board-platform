package MatchmakingLeaderboard.TicTacToe.Leaderboard;

import MatchmakingLeaderboard.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TicTacToeLeaderboard extends AbstractTicTacToeLeaderboard {
    // A map to hold player scores
    private static Map<Player, Integer> playerScores = new HashMap<>();

    // Update the score for a player based on whether they won a game
    public static void  updatePlayer(Player player, boolean won) {
        int currentScore = playerScores.getOrDefault(player, 0);
        playerScores.put(player, won ? currentScore + 1 : currentScore);
    }

    // Return a list of players sorted by scores in descending order
    @Override
    public List<Player> getScores() {
        return playerScores.entrySet().stream()
                .sorted(Map.Entry.<Player, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Optionally, implement to return just the top N players
    //@Override
    public List<Player> getTopPlayers() {
        return getScores().stream().limit(10).collect(Collectors.toList());
    }

    // Display all players and their scores
    @Override
    public void displayLeaderboard() {
        System.out.println("Leaderboard:");
        //getScores().forEach(player -> System.out.println(player.getName() + ": " + playerScores.get(player)));
    }

    // Sorting logic is integrated into the getScores method
    @Override
    public void sortLeaderboard() {
        // This method can remain empty as sorting is handled in getScores.
    }
}