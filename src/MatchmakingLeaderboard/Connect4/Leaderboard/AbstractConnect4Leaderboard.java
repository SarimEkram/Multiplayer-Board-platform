package MatchmakingLeaderboard.Connect4.Leaderboard;

import MatchmakingLeaderboard.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for Connect-4 leaderboard management.
 *
 * @author Rahnuha Nurain
 */
public abstract class AbstractConnect4Leaderboard {

    // A list to store the leaderboard data (e.g., players and their scores).
    protected List<Player> leaderboardEntries;

    /**
     * Default constructor to initialize the leaderboard list.
     */
    public AbstractConnect4Leaderboard() {
        this.leaderboardEntries = new ArrayList<>();
    }

    /**
     * Records or updates a player's statistics in the leaderboard.
     */
    public void recordLeaderboardEntry(Player player, int score) {
        // TODO: Add your leaderboard recording logic here
    }

    /**
     * Abstract method to be implemented for displaying or manipulating
     * leaderboard entries in concrete classes.
     */
    public abstract void displayLeaderboard();
}
