package MatchmakingLeaderboard;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Common matchmaking queue for players, supports all games with separate queues.
 * Each instance handles a single game's queue only.
 */
public class MatchmakingQueue {

    private final Queue<Player> queue;
    private final int gameType; // 1 = TicTacToe, 2 = Connect4, 3 = Checkers
    private boolean matchReady = false;

    public MatchmakingQueue(int gameType) {
        this.gameType = gameType;

        // choose the correct queue
        switch (gameType) {
            case 1 -> queue = new LinkedList<>(); // TicTacToe
            case 2 -> queue = new LinkedList<>(); // Connect4
            case 3 -> queue = new LinkedList<>(); // Checkers
            default -> throw new IllegalArgumentException("Invalid game type: " + gameType);
        }
    }

    /**
     * Adds a player to this queue.
     */
    public void addPlayer(Player player) {
        queue.offer(player);
    }

    /**
     * Removes a player from this queue.
     */
    public void removePlayer(Player player) {
        queue.remove(player);
    }

    /**
     * Gets the next player in the queue.
     */
    public Player getNextPlayer() {
        return queue.poll();
    }

    /**
     * signals opponent players are ready to play
     */
    public void matchReady() {
        this.matchReady = true;
    }

    /**
     * Checks if enough players are available for matchmaking.
     */
    public boolean readyToMatch(){
        return this.matchReady;
    }

    /**
     * Gets number of players currently in this queue.
     */
    public int getQueueSize() {
        return queue.size();
    }

    /**
     * Gets the game type for this queue.
     */
    public int getGameType() {
        return gameType;
    }
}
