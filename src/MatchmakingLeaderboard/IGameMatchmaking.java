package MatchmakingLeaderboard;

/**
 *Abstract base class for game leaderboard and matchmaking
 *
 * @author Manav Patel
 */

public class IGameMatchmaking {

    void matchmaking(){
        findMatch();
        while (checkMatchmaking()) {
            Player player1 = getPlayer();
            Player player2 = getPlayer();
            if (checkPlayers(player1, player2)) {
                signalAddUser();
                signalAddUser();
                signalStartGame();
            }
        }
    }

    /**
     * function for players to join the matchmaking queue
     *
     */
    void joinQueue(){

    }

    /**
     * function for players to leave the matchmaking queue
     *
     */
    void leaveQueue(){
    }

    /**
     * function that contacts the server and finds matches to play
     *
     */
    void findMatch(){
    }

    /**
     * function to signal start of game once matchmaking is completed
     *
     */
    void signalStartGame(){
    }

    /**
     * function to check if the matchmaking is complete
     *
     * @return boolean that signals completion or failure
     */
    boolean checkMatchmaking(){
        return false;
    }

    /**
     * function to signal adding player to the game
     */
    void signalAddUser(){
    }

    /**
     * function to simulate getting player from queue information for skilled based
     *
     */
    Player getPlayer(){
        return new Player(0.0,1,123456);
    }

    /**
     * Check if players are similar in skill level
     * @param Player1
     * @param Player2
     */
    boolean checkPlayers(Object Player1, Object Player2){
        return false;
    }
}
