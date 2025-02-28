package MatchmakingLeaderboard.Connect4.Matchmaking;

import MatchmakingLeaderboard.IGameMatchmaking;
import MatchmakingLeaderboard.Player;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Abstract base class for game matchmaking
 *
 * @author Happy Prajapati
 */
public abstract class AbstractConnect4Matchmaking implements IGameMatchmaking {
    protected Queue<Player> queue;

    /**
     * Constructor to initialize a queue for connect-4 game
     */
    public AbstractConnect4Matchmaking() {
        this.queue = new LinkedList<>();
    }

    /**
     * Add the matchmaking logic and exception handling.
     */
    public void matchmaking(){
    }

    /**
     * Abstract method that must be implemented in the concrete class
     */
    public abstract void findMatch();
}
