package MatchmakingLeaderboard.Checkers.Leaderboard;


import MatchmakingLeaderboard.Player;

import java.util.List;
/**
 * Concrete class for Checkers leaderboard management.
 * Extends Abstract Checkers Leaderboard
 *
 * @author Jay Thakor
 * @author Neel Savani
 */

public class CheckersLeaderboard extends AbstractCheckersLeaderboard{

    @Override
    public List<Player> getScores() {
        return List.of();
    }

    @Override
    public void updateScore(int playerId, int score) {
    }

    @Override
    public List<Player> getTopPlayers() {
        return List.of();
    }

    @Override
    public void displayLeaderboard() {

    }

}
