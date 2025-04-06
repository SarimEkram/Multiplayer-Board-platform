package MatchmakingLeaderboard.TicTacToe.Leaderboard;

import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import MatchmakingLeaderboard.GameType;
import static MatchmakingLeaderboard.GameType.TIC_TAC_TOE;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeLeaderboard extends AbstractTicTacToeLeaderboard {

    private static final TicTacToeLeaderboard instance = new TicTacToeLeaderboard();


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

    public static void updatePlayer(Player player, boolean Won, GameType gameType) {
        instance.addPlayer(player, gameType);
        instance.sortLeaderboard();
    }

    @Override
    public void displayLeaderboard() {
        PlayerDatabase.loadPlayersFromCSV();
        List<Player> allPlayers = PlayerDatabase.getAllPlayers();

        List<Player> ticTacToePlayers = new ArrayList<>();
        for (Player player : allPlayers) {
            if (player.getMMR(TIC_TAC_TOE) > 0) {
                ticTacToePlayers.add(player);
            }
        }

        this.players = ticTacToePlayers;
        sortLeaderboard();
    }

    public void sortLeaderboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getMMR(TIC_TAC_TOE), p1.getMMR(TIC_TAC_TOE)));
    }
}
