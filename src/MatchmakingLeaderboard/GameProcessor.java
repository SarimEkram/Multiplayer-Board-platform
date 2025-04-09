package MatchmakingLeaderboard;

import MatchmakingLeaderboard.Connect4.Leaderboard.Connect4Leaderboard;
import MatchmakingLeaderboard.TicTacToe.Leaderboard.TicTacToeLeaderboard;
import MatchmakingLeaderboard.Checkers.Leaderboard.CheckersLeaderboard;


/**
 * Keeps track of what happens after the game completes.
 **/
public class GameProcessor {

    private final Player winner;
    private final Player loser;
    private final GameType gameType;

    public GameProcessor(Player p1, Player p2, GameType gameType) {
        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("PLAYER INFO IS NULL");
        }

        if(gameType == null) {
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

    public GameType getType() {
        return gameType;
    }

    /**
     * Handles the end of a match with a winner and loser.
     */
    public void UpdateResults(Player winner, Player loser, GameType gameType) {

        winner.addWin(gameType);
        loser.addLoss(gameType);

        updateMMR(winner, loser, true, gameType);
        updateMMR(loser, winner, false, gameType);

        updateLeaderBoard(winner, loser, gameType);

        updateLevel(winner);
        updateLevel(loser);

        PlayerDatabase.savePlayer(winner);
        PlayerDatabase.savePlayer(loser);
    }

    /**
     * Updates level for a player
     * @param player
     *
     */
    public void updateLevel(Player player) {

        int l1 = player.getMMR(GameType.TIC_TAC_TOE);
        int l2 = player.getMMR(GameType.CONNECT_FOUR);
        int l3 = player.getMMR(GameType.CHECKERS);

        int newLevel = (l1+l2+l3)/100;
        if(newLevel == 0){
            player.setLevel(1);
        }

        player.setLevel(newLevel);

    }
    /**
     * Updates MMR for players based on game outcome.
     */
    public void updateMMR(Player p1, Player p2, boolean won, GameType gameType) {
        int updateMMR = MMRCalculator.calculateMMR(p1, p2, won, gameType);
        int newMMR = p1.getMMR(gameType) + updateMMR;

        if(newMMR < 0) {
            p1.setMMR(0, gameType);
        }

        p1.setMMR(newMMR,gameType);

        Rank rank = p1.getRank(gameType);
        rank.adjustPoints(p1,updateMMR,gameType);
    }



    /**
     * Updates the leaderboard after a match.
     */
    private void updateLeaderBoard(Player winner, Player loser, GameType gameType) {
        switch (gameType) {
            case TIC_TAC_TOE:
                TicTacToeLeaderboard.updatePlayer(winner, true, gameType);
                TicTacToeLeaderboard.updatePlayer(loser, false, gameType);
                break;
            case CONNECT_FOUR:
                Connect4Leaderboard.updatePlayer(winner, true, gameType);
                Connect4Leaderboard.updatePlayer(loser, false, gameType);
                break;
            case CHECKERS:
                CheckersLeaderboard.updatePlayer(winner, true, gameType);
                CheckersLeaderboard.updatePlayer(loser, false, gameType);
                break;
        }
    }

    /**
     * Handles the end of a draw match.
     */
    public void ProcessDraw(Player p1, Player p2, GameType gameType) {
        int updateMMR1 = MMRCalculator.calculateDraw(p1, p2, gameType);
        int updateMMR2 = MMRCalculator.calculateDraw(p2, p1, gameType);

        p1.setMMR(p1.getMMR(gameType) + updateMMR1, gameType);
        p2.setMMR(p2.getMMR(gameType) + updateMMR2, gameType);

        updateLeaderBoard(p1, p2, gameType);

        PlayerDatabase.savePlayer(p1);
        PlayerDatabase.savePlayer(p2);
    }
}
