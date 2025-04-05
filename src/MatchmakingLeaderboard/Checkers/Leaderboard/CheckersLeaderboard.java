package MatchmakingLeaderboard.Checkers.Leaderboard;

import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete class for Checkers leaderboard management.
 */
public class CheckersLeaderboard extends AbstractCheckersLeaderboard {

    private static final CheckersLeaderboard instance = new CheckersLeaderboard();
    private static final int GAME_TYPE = 3;

    private CheckersLeaderboard() {
    }

    public static CheckersLeaderboard getInstance() {
        return instance;
    }

    @Override
    public List<Player> getScores() {
        sortLeaderboard();
        return new ArrayList<>(players);
    }

    public static void updatePlayer(Player player, boolean Won, int gameType) {
        instance.addPlayer(player, gameType);
        instance.sortLeaderboard();
    }

    @Override
    public void displayLeaderboard() {
        PlayerDatabase.loadPlayersFromCSV();
        List<Player> allPlayers = PlayerDatabase.getAllPlayers();

        List<Player> checkersPlayers = new ArrayList<>();
        for (Player player : allPlayers) {
            if (player.getMMR(GAME_TYPE) > 0) {
                checkersPlayers.add(player);
            }
        }

        this.players = checkersPlayers;
        sortLeaderboard();
    }

    public void sortLeaderboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getMMR(GAME_TYPE), p1.getMMR(GAME_TYPE)));
    }
}
