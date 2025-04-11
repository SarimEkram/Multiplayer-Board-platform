package MatchmakingLeaderboard.Connect4.Leaderboard;


import MatchmakingLeaderboard.Player;
import java.util.ArrayList;
import java.util.List;
import MatchmakingLeaderboard.IGameLeaderboard;
import MatchmakingLeaderboard.GameType;

/**
 * Abstract base class for Connect-4 leaderboard management.
 *
 * @author Rahnuha Nurain
 * @author Neel Savani
 */

public abstract class AbstractConnect4Leaderboard implements IGameLeaderboard {
    protected List<Player> players = new ArrayList<>();
    protected static final GameType gameType = GameType.CONNECT_FOUR;


    /**
     * Adds a player to the leaderboard if they aren't already present.
     * @param player
     * @param gameType
     */
    public void addPlayer(Player player, GameType gameType) {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        if (!players.contains(player)) {
            players.add(player);
        }
    }


    /**
     * Retrieves the leaderboard scores.
     */
    public abstract List<Player> getScores();


    /**
     * Finds a player in the leaderboard by their unique ID.
     * @param playerId
     * @return
     */
    public Player findPLayerById(int playerId) {
        for (Player player : players) {
            if (player.getUserID() == playerId) {
                return player;
            }
        }
        return null;
    }
}
