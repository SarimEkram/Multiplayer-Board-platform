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

    // This method retrives the list of top  10 player based on score
    @Override
    public  List <Player> getTopPlayers(){
        List <Player> SortedAllPlayersList = getScores(); // Already sorted the list in Descending order
        List <Player> topPlayers = new ArrayList<>();

        for (int i = 0, i < 10 && i < allPlayers.size(), i++){  // Iterate over the score list of all players
            topPlayers.add(allPlayers.get(i));   // Adding score of players to top topPlayer list
            }
        return topPlayers
        }

    @Override
    public void displayLeaderboard() {
        System.out.println(" Connect4 Leaderboard");
        List <Player> sortedAllPlayerList = getScores();

        int rank = 1;
        for (Player player : sortedAllPlayerList){
            int score = playerScores.get(player);
            int win = player.getWins()[1];
            int losses = player.getLosses[1];
            System.out.println("%d | UserID %d | Score: %d | Wins: %d | Losses: %d%n",
                    rank, player.getUserID(), score, mmr, win, losses);)
            rank++
        }

    }


}
