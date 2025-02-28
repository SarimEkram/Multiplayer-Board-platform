package MatchmakingLeaderboard.Checkers.Leaderboard;

import MatchmakingLeaderboard.IGameLeaderboard;
import MatchmakingLeaderboard.Player;

/**
 * Abstract class for Checkers Leaderboard.
 * Implements Game Leaderboard functionality.
 *
 * @author Jay Thakor
 */
public abstract class AbstractCheckersLeaderboard implements IGameLeaderboard {
    @Override
    public List<Player> getScores() {
    }

    @Override
    public void updateScore(int playerId, int score) {
    }

    @Override
    public List<Player> getTopPlayers() {
    }

    @Override
    public void displayLeaderboard() {
    }

}
