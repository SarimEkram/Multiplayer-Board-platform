package MatchmakingLeaderboard.Checkers.Matchmaking;

import MatchmakingLeaderboard.IGameMatchmaking;
import MatchmakingLeaderboard.Player;

/**
 * Abstract class for Checkers Matchmaking
 * Implements Game Matchmaking functionality.
 *
 * @author Jay Thakor
 */
public abstract class AbstractCheckersMatchmaking implements IGameMatchmaking {

    @Override
    public void joinQueue(int playerID) {
    }

    @Override
    public void leaveQueue(int playerID) {
    }

    @Override
    public void findMatch() {
    }

    @Override
    public void signalStartGame() {
    }

    @Override
    public boolean checkMatchmaking() {
    }

    @Override
    public void signalAddPlayer() {
    }

    @Override
    public Player getPlayer() {
    }

    @Override
    public boolean checkPlayers(Player Player1, Player Player2) {
    }
}
