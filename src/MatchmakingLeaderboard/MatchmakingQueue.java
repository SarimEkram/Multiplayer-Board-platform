package MatchmakingLeaderboard;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Common matchmaking queue for players to join the game.
 * This will be used for multiple games.
 */
public class MatchmakingQueue {
    private Queue<Player> queue;

    /**
     * constructor initializes a new queue
     */
    public MatchmakingQueue() {
        queue = new LinkedList<>();
    }

    /**
     * Adds a player to the queue.
     */
    public void addPlayer(Player player) {
    }

    /**
     * Removes a player from the queue.
     */
    public void removePlayer(Player player) {
    }

    /**
     * removes the first player in the queue (who will match with another player).
     * @return The player to match.
     */
    public Player getNextPlayer() {
        return queue.poll();
    }

    /**
     * Checks if there are enough players to create a match.
     * @return true if there are enough players, false otherwise.
     */
    public boolean readyToMatch() {
        if (this.getQueueSize() > 1){
            return true;
        }else {
            return false;
        }
    }

    /**
     * Get the number of players in the queue.
     * @return The size of the queue.
     */
    public int getQueueSize() {
        return queue.size();
    }
}
