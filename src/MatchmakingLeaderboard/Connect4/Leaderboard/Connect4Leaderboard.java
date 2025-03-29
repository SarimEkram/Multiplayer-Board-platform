package MatchmakingLeaderboard.Connect4.Leaderboard;

import MatchmakingLeaderboard.Player;

/**
 * Concrete class for Connect-4 leaderboard management.
 *
 * @author Rahnuha Nurain
 */
    public class Connect4Leaderboard extends AbstractConnect4Leaderboard {

    // A map to hold players score
    private static Map<Player, Integer> playerScores = new HashMap<>();

    // Return a list of players sorted by scores in descending order
    @Override
    public List<Player> getScores() {
        return playerScores.entrySet().stream()
                .sorted(Map.Entry.<Player, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

    @Override
    public void displayLeaderboard() {
    }

    public static void updatePlayer(Player player, boolean Won){

    }
    public void removePlayerEntry(Player player) {
}
}
