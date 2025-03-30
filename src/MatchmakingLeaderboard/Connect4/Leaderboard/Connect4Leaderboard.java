package MatchmakingLeaderboard.Connect4.Leaderboard;

import MatchmakingLeaderboard.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Concrete class for Connect-4 leaderboard management.
 * @author Rahnuha Nurain
 */
public class Connect4Leaderboard extends AbstractConnect4Leaderboard {

    // A map to hold players' scores
    private Map<Player, Integer> playerScores = new HashMap<>();

    // Return a list of players sorted by scores in descending order
    @Override
    public List<Player> getScores() {
        return playerScores.entrySet().stream()
                .sorted(Map.Entry.<Player, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // This method retrieves the list of top 10 players based on score
    public List<Player> getTopPlayers() {
        List<Player> allPlayers = getScores(); // Already sorted the list in descending order
        List<Player> topPlayers = new ArrayList<>();
        for (int i = 0; i < 10 && i < allPlayers.size(); i++) {  // Iterate over the score list of all players
            topPlayers.add(allPlayers.get(i));   // Adding players to topPlayer list
        }
        return topPlayers;
    }

    @Override
    public void displayLeaderboard() {
        System.out.println("Connect4 Leaderboard");
        List<Player> sortedAllPlayersList = getScores();
        int rank = 1;
        for (Player player : sortedAllPlayersList) {
            int score = playerScores.get(player);
            System.out.printf("%d | UserID %d | Score: %d | Wins: %d | Losses: %d%n",
                    rank, player.getUserID(), score, player.getWins(), player.getLosses());
            rank++;
        }
    }
}
