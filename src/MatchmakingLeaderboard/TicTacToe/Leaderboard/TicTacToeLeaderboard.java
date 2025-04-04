package MatchmakingLeaderboard.TicTacToe.Leaderboard;

import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeLeaderboard extends AbstractTicTacToeLeaderboard {

    private static final TicTacToeLeaderboard instance = new TicTacToeLeaderboard();
    private static final int GAME_TYPE = 1;

    private TicTacToeLeaderboard() {
    }

    public static TicTacToeLeaderboard getInstance() {
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

        List<Player> ticTacToePlayers = new ArrayList<>();
        for (Player player : allPlayers) {
            if (player.getMMR(GAME_TYPE) > 0) {
                ticTacToePlayers.add(player);
            }
        }

        this.players = ticTacToePlayers;
        sortLeaderboard();
    }

    public void sortLeaderboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getMMR(GAME_TYPE), p1.getMMR(GAME_TYPE)));
    }
}
