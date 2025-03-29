package MatchmakingLeaderboard.Checkers.Matchmaking;

import MatchmakingLeaderboard.*;

import java.util.Random;

public class CheckersMatchmaking extends AbstractCheckersMatchmaking{
    int gameType = 3;
    private boolean matchmakingUp = false;
    private final double probabilityOfNetworkFailure = 0.117;
    private MatchmakingQueue queue;


    /**
     * Class that represents the players joining the matchmaking queue
     * It continuously scans for player join signals from the database
     *
     */
    public CheckersMatchmaking() throws Exception {
        for (int j = 0; j < 100; j++) {
            Random random = new Random();
            double randomValue = random.nextDouble();
            if (probabilityOfNetworkFailure <= randomValue) {
                throw new NetworkFailureException("Network Error! Could not connect to servers");
            }
        }

        try {
            queue = new MatchmakingQueue();
            this.matchmakingUp = true;
        }catch (Exception e){
            this.matchmakingUp = false;
            throw new MatchmakingException("Matchmaking is Down");
        }
        try {
            while (true) {
                // replace with a function to get players from database
                this.joinQueue(new Player(56, 123456));
                this.matchmakingUp = true;
                wait(2500);
            }
        }catch (Exception e){
            this.matchmakingUp = false;
            throw new MatchmakingException("Something went wrong!");
        }
    }

    /**
     * function that simulates matchmaking for TicTacToe
     */
    public void startMatchmaking(){
        if (checkMatchmaking()) {
            Player player = queue.getNextPlayer();
            boolean iscompatible = false;
            while (iscompatible) {
                iscompatible = checkPlayers(player, queue.getNextPlayer());
            }
            if (queue.readyToMatch()) {
                findMatch();
                // TODO : add a game class to add players to the simulation
                signalStartGame();
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
    public void findMatch() {
        System.out.println("Generate a request to the backend asking for an unpopulated game simulation");
    }

    /**
     * function to signal the game subsystem to start the game simulation
     */
    @Override
    public void signalStartGame() {
        System.out.println("Signal game system to start the game simulation");
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
        if ((player1.getRank(gameType) == player2.getRank(gameType)) && (player1.getGameSignal(gameType) == player2.getGameSignal(gameType))){
            return Math.abs((player1.getLevel() - player2.getLevel())) == 10;
        }
        return false;
    }
}
