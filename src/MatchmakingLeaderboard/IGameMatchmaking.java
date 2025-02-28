package MatchmakingLeaderboard;

/**
 *Abstract base class for game leaderboard and matchmaking
 *
 * @author Manav Patel
 */

public interface IGameMatchmaking {

    void matchmaking();

    /**
     * function for players to join the matchmaking queue
     *
     */
    void joinQueue();

    /**
     * function for players to leave the matchmaking queue
     *
     */
    void leaveQueue();

    /**
     * function that contacts the server and finds matches to play
     *
     */
    void findMatch();

    /**
     * function to signal start of game once matchmaking is completed
     *
     */
    void signalStartGame();

    /**
     * function to check if the matchmaking is complete
     *
     * @return boolean that signals completion or failure
     */
    boolean checkMatchmaking();

    /**
     * function to signal adding player to the game
     */
    void signalAddPlayer();

    /**
     * function to simulate getting player from queue information for skilled based
     *
     */
    Player getPlayer();

    /**
     * Check if players are similar in skill level
     *
     */
    boolean checkPlayers(int Player1, int Player2);
}
