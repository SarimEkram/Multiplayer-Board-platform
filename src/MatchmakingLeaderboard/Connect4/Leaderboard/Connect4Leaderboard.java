package MatchmakingLeaderboard.Connect4.Leaderboard;

import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import MatchmakingLeaderboard.GameType;
import java.util.ArrayList;
import java.util.List;

import static MatchmakingLeaderboard.GameType.CONNECT_FOUR;
/**
 * Concrete class for Connect-4 leaderboard management.
 */
public class Connect4Leaderboard extends AbstractConnect4Leaderboard {

    private static final Connect4Leaderboard instance = new Connect4Leaderboard();


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

    public static void updatePlayer(Player player, boolean Won, GameType gameType) {
        instance.addPlayer(player, gameType);
        instance.sortLeaderboard();
    }

    @Override
    public void displayLeaderboard() {
        PlayerDatabase.loadPlayersFromCSV();
        List<Player> allPlayers = PlayerDatabase.getAllPlayers();

        List<Player> connect4Players = new ArrayList<>();
        for (Player player : allPlayers) {
            if (player.getMMR(CONNECT_FOUR) > 0) {
                connect4Players.add(player);
            }
        }

        this.players = connect4Players;
        sortLeaderboard();
    }

    public void sortLeaderboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getMMR(CONNECT_FOUR), p1.getMMR(CONNECT_FOUR)));
    }
}
