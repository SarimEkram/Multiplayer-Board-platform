package MatchmakingLeaderboard.TicTacToe.Leaderboard;

import MatchmakingLeaderboard.Player;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TicTacToeLeaderboard extends AbstractTicTacToeLeaderboard {

    private Map<Player, Integer> playerScores = new HashMap<>();

    @Override
    public List<Player> getScores() {
        return playerScores.entrySet().stream()
                .sorted(Map.Entry.<Player, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static void updatePlayer(Player player, boolean Won){

    }

    @Override
    public List<Player> getTopPlayers() {
        return List.of();
    }

    @Override
    public void displayLeaderboard() {

    }

    @Override
    public void sortLeaderboard() {

    }
}
