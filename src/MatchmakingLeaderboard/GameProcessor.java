package MatchmakingLeaderboard;

import MatchmakingLeaderboard.TicTacToe.Leaderboard.TicTacToeLeaderboard;
import MatchmakingLeaderboard.Connect4.Leaderboard.Connect4Leaderboard;
import MatchmakingLeaderboard.Checkers.Leaderboard.CheckersLeaderboard;

/**
 * Keeps track of what happens after the game completes.
 **/
public class GameProcessor {

    private final Player winner;
    private final Player loser;
    private final int gameType;

    public GameProcessor(Player p1, Player p2, int gameType) {
        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("PLAYER INFO IS NULL");
        }

        if(gameType < 1 || gameType > 3) {
            throw new IllegalArgumentException("GAME TYPE IS INVALID");
        }

        this.winner = p1;
        this.loser = p2;
        this.gameType = gameType;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getLoser() {
        return loser;
    }

    public int getType() {
        return gameType;
    }

    /**
     * Handles the end of a match with a winner and loser.
     */
    public static void UpdateResults(Player winner, Player loser, int gameType) {

        winner.addWin(gameType);
        loser.addLoss(gameType);

        updateMMR(winner, loser, true, gameType);
        updateMMR(loser, winner, false, gameType);
        updateLeaderBoard(winner, loser, gameType);
    }

    /**
     * Updates MMR for players based on game outcome.
     */
    public static void updateMMR(Player p1, Player p2, boolean won, int gameType) {
        int updateMMR = MMRCalculator.calculateMMR(p1, p2, won, gameType);
        int newMMR = p1.getMMR(gameType) + updateMMR;

        if(newMMR < 0) {
            p1.setMMR(0, gameType);
        }

        p1.setMMR(newMMR,gameType);

        Rank rank = p1.getRank(gameType);
        Rank.adjustPoints(p1,updateMMR,gameType);
    }



    /**
     * Updates the leaderboard after a match.
     */
    private static void updateLeaderBoard(Player winner, Player loser, int gameType) {
        switch (gameType) {
            case 1:
                TicTacToeLeaderboard.updatePlayer(winner, true);
                TicTacToeLeaderboard.updatePlayer(loser, false);
                break;
            case 2:
                Connect4Leaderboard.updatePlayer(winner, true);
                Connect4Leaderboard.updatePlayer(loser, false);
                break;
            case 3:
                CheckersLeaderboard.updatePlayer(winner, true);
                CheckersLeaderboard.updatePlayer(loser, false);
                break;
        }
    }

    /**
     * Handles the end of a draw match.
     */
    public static void ProcessDraw(Player p1, Player p2, int gameType) {
        int updateMMR1 = MMRCalculator.calculateDraw(p1, p2, gameType);
        int updateMMR2 = MMRCalculator.calculateDraw(p2, p1, gameType);

        p1.setMMR(p1.getMMR(gameType) + updateMMR1, gameType);
        p2.setMMR(p2.getMMR(gameType) + updateMMR2, gameType);

        updateLeaderBoard(p1, p2, gameType);
    }
}
