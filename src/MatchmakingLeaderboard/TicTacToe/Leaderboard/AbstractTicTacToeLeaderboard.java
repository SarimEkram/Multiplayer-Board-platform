package MatchmakingLeaderboard.TicTacToe.Leaderboard;

import MatchmakingLeaderboard.IGameLeaderboard;
import MatchmakingLeaderboard.GameType;
import MatchmakingLeaderboard.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for TicTacToe Leaderboard.
 * Implements Game Leaderboard functionality.
 *
 */
public abstract class AbstractTicTacToeLeaderboard implements IGameLeaderboard {
    protected List<Player> players = new ArrayList<>();
    protected static final GameType gameType = GameType.TIC_TAC_TOE;

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
     * @return player
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


