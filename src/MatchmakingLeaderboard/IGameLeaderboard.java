package MatchmakingLeaderboard;

import java.util.List;

/**
 * Interface for game leaderboards.
 * Defines essential methods for leaderboard.
 *
 * @author Happy Prajapati
 */
public interface IGameLeaderboard {

    /**
     * Returns the scores or rankings of players.
     *
     * @return List of player scores/rankings.
     */
    List<Player> getScores();

    /**
     * Updates a player's score in the leaderboard.
     */
    void updatePlayer(Player player, boolean Won);

    /**
     * Retrieves the top-ranked players from the leaderboard.
     *
     * @return List of top players.
     */
    List<Player> getTopPlayers();


    /**
     * Displays the leaderboard sorted by rank.
     */
    void displayLeaderboard();

    void sortLeaderboard();
}
