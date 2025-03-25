package MatchmakingLeaderboard.Checkers.Leaderboard;

import MatchmakingLeaderboard.IGameLeaderboard;
import MatchmakingLeaderboard.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for Checkers Leaderboard.
 * Implements Game Leaderboard functionality.
 *
 * @author Jay Thakor
 * @author Neel Savani
 * @author Happy Prajapati
 */
public abstract class AbstractCheckersLeaderboard implements IGameLeaderboard {
    protected List<Player> players = new ArrayList<>();

    public abstract void updateScore(int playerId, int score);

    public abstract List<Player> getScores();

    public abstract List<Player> getTopPlayers();


}
