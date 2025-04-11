package MatchmakingLeaderboard.Checkers.Leaderboard;


import MatchmakingLeaderboard.GameType;
import MatchmakingLeaderboard.IGameLeaderboard;
import MatchmakingLeaderboard.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for Checkers Leaderboard.
 * Implements Game Leaderboard functionality.
 *
 *
 * @author Happy Prajapati
 */
public abstract class AbstractCheckersLeaderboard implements IGameLeaderboard {
    protected List<Player> players = new ArrayList<>();
    protected static GameType  gameType = GameType.CHECKERS;


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




