package MatchmakingLeaderboard.TicTacToe.Matchmaking;

import MatchmakingLeaderboard.MatchmakingQueue;
import MatchmakingLeaderboard.Player;

public class TicTacToeMatchmaking extends AbstractTicTacToeMatchmaking{

    boolean matchmakingUp = false;

    MatchmakingQueue queue = new MatchmakingQueue();

    /**
     * Class that simulates matchmaking
     */
    public TicTacToeMatchmaking() {
        if (checkMatchmaking()) {
            Player player = queue.getNextPlayer();
            boolean iscompatible = false;
            while (iscompatible) {
                iscompatible = checkPlayers(player, queue.getNextPlayer());
            }
            if (queue.readyToMatch()) {
                findMatch();
                signalStartGame();
            } else {
                System.out.println("Matchmaking cancelled! Not enough players...please try again!");
            }
        } else {
            System.out.println("Matchmaking is down. Please try again in some time. The issue has been reported");
        }
    }

    @Override
    public void joinQueue(Player player) {
        queue.addPlayer(player);
    }

    @Override
    public void leaveQueue(Player player) {
        queue.removePlayer(player);
    }

    @Override
    public void findMatch() {
    }

    @Override
    public void signalStartGame() {
    }

    @Override
    public boolean checkMatchmaking() {
        return this.matchmakingUp;
    }

    @Override
    public void signalAddPlayer() {
    }

    @Override
    public boolean checkPlayers(Player player1, Player player2) {
        if ((player1.getRank() == player2.getRank()) && (player1.getGameSignal() == player2.getGameSignal())){
            if (Math.abs((player1.getLevel() - player2.getLevel())) == 10 ){
                return true;
            }
        }
        return false;
    }
}
