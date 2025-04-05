package MatchmakingLeaderboard.Connect4.Leaderboard;

import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete class for Connect-4 leaderboard management.
 */
public class Connect4Leaderboard extends AbstractConnect4Leaderboard {

    private static final Connect4Leaderboard instance = new Connect4Leaderboard();
    private static final int GAME_TYPE = 2;

    private Connect4Leaderboard() {
    }

    public static Connect4Leaderboard getInstance() {
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

        List<Player> connect4Players = new ArrayList<>();
        for (Player player : allPlayers) {
            if (player.getMMR(GAME_TYPE) > 0) {
                connect4Players.add(player);
            }
        }

        this.players = connect4Players;
        sortLeaderboard();
    }

    public void sortLeaderboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getMMR(GAME_TYPE), p1.getMMR(GAME_TYPE)));
    }
}
