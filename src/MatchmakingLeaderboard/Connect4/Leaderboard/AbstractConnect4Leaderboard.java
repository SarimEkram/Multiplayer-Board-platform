package MatchmakingLeaderboard.Connect4.Leaderboard;

import MatchmakingLeaderboard.Player;
import java.util.ArrayList;
import java.util.List;
import MatchmakingLeaderboard.IGameLeaderboard;
/**
 * Abstract base class for Connect-4 leaderboard management.
 *
 * @author Rahnuha Nurain
 * @author Neel Savani
 */
public abstract class AbstractConnect4Leaderboard implements IGameLeaderboard {
    protected List<Player> players = new ArrayList<>();
    protected static final int gameType = 2;


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
