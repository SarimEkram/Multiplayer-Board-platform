package MatchmakingLeaderboard.Connect4.Matchmaking;

import MatchmakingLeaderboard.*;

import java.io.IOException;
import java.util.Random;

public class Connect4Matchmaking extends AbstractConnect4Matchmaking{
    int gameType = 2;
    protected boolean matchmakingUp = false;
    private final double probabilityOfNetworkFailure = 0.0210;
    private MatchmakingQueue queue;

    /**
     * Constructor class for Connect4 Matchmaking
     */
    public Connect4Matchmaking(){
        queue = new MatchmakingQueue(gameType);
    }

    /**
     * Class that represents the players joining the matchmaking queue
     * It continuously scans for player joins signals from the database and throws exceptions if
     * matchmaking is down, thus preventing other players from joining
     */
    public void matchmakingConnect() throws IOException {
        Random random = new Random();
        double randomValue = random.nextDouble();
        if (probabilityOfNetworkFailure >= randomValue) {
            throw new NetworkFailureException("Network Error! Could not connect to servers");
        }

        try {
            queue = new MatchmakingQueue(gameType);
            this.matchmakingUp = true;
        }catch (Exception e){
            this.matchmakingUp = false;
            throw new MatchmakingException("Matchmaking is Down");
        }
    }

    /**
     * function that simulates matchmaking for TicTacToe
     */
    public void startMatchmaking(){
        if (checkMatchmaking()) {
            Player player1 = queue.getNextPlayer();
            Player player2 = findOpponent(player1.getUserID());
//            boolean iscompatible = checkPlayers(player1, player2);
//            while (iscompatible) {
//                player2 = queue.getNextPlayer();
//                iscompatible = checkPlayers(player1, player2);
//            }
            queue.matchReady();
            if (queue.readyToMatch()) {
                this.findMatch(player1, player2);
                this.signalStartGame();
            } else {
                System.out.println("Matchmaking cancelled! Not enough players...please try again!");
            }
        } else {
            System.out.println("Matchmaking is down. Please try again in some time. The issue has been reported");
        }
    }

    /**
     * function used by players to join the queue
     *
     * @param player player to add to the queue
     */
    @Override
    public void joinQueue(Player player) {
        queue.addPlayer(player);
    }

    public Player findOpponent(int playerID) {
        Player player1 = PlayerDatabase.getPlayerByUserID(playerID);

        Player player2 = queue.getNextPlayer();
        System.out.println("1");
        boolean iscompatible = checkPlayers(player1, player2);
        while (!iscompatible) {
            System.out.println("2");
            player2 = queue.getNextPlayer();
            iscompatible = checkPlayers(player1, player2);
        }
        return player2;
    }
    /**
     * Function used by player to leave the matchmaking queue
     *
     * @param player player to remove from the queue
     */
    @Override
    public void leaveQueue(Player player) {
        queue.removePlayer(player);
    }

    /**
     * function to find matched players an unpopulated game simulation ready to play
     */
    @Override
    public void findMatch(Player Player1, Player Player2) {
        System.out.println("Generate a request to the backend asking for an unpopulated game simulation");
    }

    /**
     * function to signal the game subsystem to start the game simulation
     */
    @Override
    public void signalStartGame() {
        System.out.println("Signal game system to start the game simulation and load matched players");
    }

    /**
     * function to check if the matchmaking system is up
     *
     * @return boolean representing status of matchmaking system
     */
    @Override
    public boolean checkMatchmaking() {
        return this.matchmakingUp;
    }

    /**
     * function to add players to the game simulation
     *
     * @param player the player to add to simulation
     */
    @Override
    public void signalAddPlayer(Player player) {
        System.out.printf("Signal database to add player to the game simulation %d ", player.getUserID());
    }

    /**
     * function to check if the two players are comparable in their skills and stats
     * @param player1 the first player added to queue chronologically
     * @param player2 the second player added to queue chronologically
     * @return boolean representing compatibility
     */
    @Override
    public boolean checkPlayers(Player player1, Player player2) {
        if ((player1.getRank(gameType).getCurrentTier() == player2.getRank(gameType).getCurrentTier()) && (player1.getGameSignal(gameType) == player2.getGameSignal(gameType))&&(player1.getUserID()!= player2.getUserID())){
            return Math.abs((player1.getLevel() - player2.getLevel())) <= 10;
        }
        return false;
    }


}
