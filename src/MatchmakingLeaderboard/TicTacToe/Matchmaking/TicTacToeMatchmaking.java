package MatchmakingLeaderboard.TicTacToe.Matchmaking;

import MatchmakingLeaderboard.Player;

public class TicTacToeMatchmaking extends AbstractTicTacToeMatchmaking{


    @Override
    public void joinQueue(int PlayerID) {
    }

    @Override
    public void leaveQueue(int PlayerID) {
    }

    @Override
    public void findMatch() {
    }

    @Override
    public void signalStartGame() {
    }

    @Override
    public boolean checkMatchmaking() {
        return false;
    }

    @Override
    public void signalAddPlayer() {
    }

    @Override
    public Player getPlayer() {
        return null;
    }

    @Override
    public boolean checkPlayers(Player Player1, Player Player2) {
        return false;
    }
}
