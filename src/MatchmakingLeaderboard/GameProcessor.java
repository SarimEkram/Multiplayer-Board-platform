package MatchmakingLeaderboard;
import MatchmakingLeaderboard.TicTacToe.Leaderboard.TicTacToeLeaderboard;
import MatchmakingLeaderboard.Connect4.Leaderboard.Connect4Leaderboard;
import MatchmakingLeaderboard.Checkers.Leaderboard.CheckersLeaderboard;
/**
 * Keeps track of what happens after the game completes.
 **/
public class GameProcessor {

    private Player winner;
    private Player loser;

    private int gameType;

    public GameProcessor(Player p1, Player p2, int gameType) {
        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("PLAYER INFO IS NULL");
        }
        this.winner = p1;
        this.loser = p2;
        this.gameType = gameType;
    }


    /**
     * Getters from Game Processors
     * @return
     */
    public Player getWinner(){
        return winner;
    }
    public Player getLoser(){
        return loser;
    }
    public int getType(){
        return gameType;
    }

    /**
     * Setters
     *
     */
    public static void UpdateResults(Player winner, Player loser, int gameType){
        updateMMR(winner, loser, true);
        updateMMR(loser, winner, false);
        updateRank(winner);
        updateRank(loser);
        updateLeaderBoard(winner, loser, gameType);
    }

    /**
     * Updates MMR for players based on game outcome
     *
     * @param p1
     * @param p2
     * @param Won
     */
    private static void updateMMR(Player p1, Player p2, boolean Won){
        int updateMMR = MMRCalculator.calculateMMR(p1, p2, Won);
        p1.setMMR(p1.getMMR() + updateMMR);
    }

    /**
     * Updates Rank of the Player.
     *
     * @param player
     */
    private static void updateRank(Player player){
        player.getRank().adjustPoints(player.getMMR());
    }

    private static void updateLeaderBoard(Player winner, Player loser, int gameType){
        switch (gameType){
            case 1:
                TicTacToeLeaderboard.updatePlayer(winner, true);
                TicTacToeLeaderboard.updatePlayer(loser, false);
            case 2:
                Connect4Leaderboard.updatePlayer(winner, true);
                Connect4Leaderboard.updatePlayer(loser, false);
            case 3:
                CheckersLeaderboard.updatePlayer(winner, true);
                CheckersLeaderboard.updatePlayer(loser, false);
        }
    }

    /**
     * Updates results after draw.
     *
     * @param p1
     * @param p2
     * @param gameType
     */
    public static void ProcessDraw(Player p1, Player p2, int gameType){
        int updateMMR1 = MMRCalculator.calculateDraw(p1, p2);
        int updateMMR2 = MMRCalculator.calculateDraw(p2, p1);

        p1.setMMR(p1.getMMR() + updateMMR1);
        p2.setMMR(p2.getMMR() + updateMMR2);

        updateLeaderBoard(p1, p2, gameType);
    }
}