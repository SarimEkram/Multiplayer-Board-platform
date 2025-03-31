package MatchmakingLeaderboard.Checkers.Leaderboard;

import MatchmakingLeaderboard.IGameLeaderboard;
import MatchmakingLeaderboard.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for Checkers Leaderboard.
 * Implements Game Leaderboard functionality.
 *
 * @author Jay Thakor
 * @author Neel Savani
 * @author Happy Prajapati
 */
public abstract class AbstractCheckersLeaderboard implements IGameLeaderboard {
    protected List<Player> players = new ArrayList<>();
    protected static final int gameType = 3;


    public void addPlayer(Player player, int gameType) {
        if (player == null) {
            throw new NullPointerException("player is null");
        }
        if (!players.contains(player)) {
            players.add(player);
        }
    }

    //public abstract void updatePlayer(Player player, boolean won);

    public abstract List<Player> getScores();

    //public abstract List<Player> getTopPlayers();

    public Player findPLayerById(int playerId) {
        for (Player player : players) {
            if (player.getUserID() == playerId) {
                return player;
            }
        }
        return null;
    }
}




