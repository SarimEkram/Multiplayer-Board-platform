package MatchmakingLeaderboard.Checkers.Leaderboard;

import MatchmakingLeaderboard.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Concrete class for Checkers leaderboard management.
 * Extends Abstract Checkers Leaderboard
 *
 * @author Jay Thakor
 * @author Neel Savani
 * @author Happy Prajapati
 *
 */
public class CheckersLeaderboard extends AbstractCheckersLeaderboard {

    private Map<Player, Integer> playerScores = new HashMap<>();

    @Override
    public List<Player> getScores() {
        sortLeaderboard();
        return new ArrayList<>(players);  // Return a copy of the sorted players list
    }

    public void updatePlayer(Player player, boolean won) {
        // Update or initialize player's score
        playerScores.put(player, playerScores.getOrDefault(player, 0) + (won ? 1 : 0));
        if (!players.contains(player)) {
            players.add(player);
        }
    }

    @Override
    public List<Player> getTopPlayers() {
        sortLeaderboard();
        return players.stream().limit(10).collect(Collectors.toList());  // Return the top 10 players
    }

    @Override
    public void displayLeaderboard() {
        System.out.println("Checkers Leaderboard:");
        int rank = 1;
        for (Player player : players) {
            System.out.printf("%d. %s - Score: %d\n", rank, player.getName(), playerScores.get(player));
            rank++;
        }
    }

    public void sortLeaderboard() {
        // Sort players based on their scores in descending order
        players.sort((p1, p2) -> playerScores.get(p2).compareTo(playerScores.get(p1)));
    }

    private Player findPlayerById(int playerId) {
        // Find player by their ID
        return players.stream()
                .filter(player -> player.getUserID() == playerId)
                .findFirst()
                .orElse(null);
    }
}
