package MatchmakingLeaderboard;

/**
 *Interface class for game leaderboard and matchmaking
 *
 * @author Manav Patel
 */

public interface IGameMatchmaking {

    /**
     * function for players to join the matchmaking queue
     */
    void joinQueue(Player player);

    /**
     * function for players to leave the matchmaking queue
     *
     */
    void leaveQueue(Player player);

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
     * Check if players are similar in skill level
     *
     */
    boolean checkPlayers(Player Player1, Player Player2);
}
