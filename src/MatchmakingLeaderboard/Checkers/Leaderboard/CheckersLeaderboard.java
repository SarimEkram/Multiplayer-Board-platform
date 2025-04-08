package MatchmakingLeaderboard.Checkers.Leaderboard;

import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import MatchmakingLeaderboard.GameType;

import java.util.ArrayList;
import java.util.List;

import static MatchmakingLeaderboard.GameType.CHECKERS;

/**
 * Concrete class for Checkers leaderboard management.
 */
public class CheckersLeaderboard extends AbstractCheckersLeaderboard {

    private static final CheckersLeaderboard instance = new CheckersLeaderboard();


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

    public static void updatePlayer(Player player, boolean Won, GameType gameType) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        instance.addPlayer(player, gameType);
        instance.sortLeaderboard();
    }

    @Override
    public void displayLeaderboard() {
        PlayerDatabase.loadPlayersFromCSV();
        List<Player> allPlayers = PlayerDatabase.getAllPlayers();

        List<Player> checkersPlayers = new ArrayList<>();
        for (Player player : allPlayers) {
            if (player.getMMR(CHECKERS) >= 0) {
                checkersPlayers.add(player);
            }
        }

        this.players = checkersPlayers;
        sortLeaderboard();
    }

    public void sortLeaderboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getMMR(CHECKERS), p1.getMMR(CHECKERS)));
    }
}
