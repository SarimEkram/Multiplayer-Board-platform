package MatchmakingLeaderboard.TicTacToe.Leaderboard;

import MatchmakingLeaderboard.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import MatchmakingLeaderboard.PlayerDatabase;

public class TicTacToeLeaderboard extends AbstractTicTacToeLeaderboard {
    private static final TicTacToeLeaderboard instance = new TicTacToeLeaderboard();

    private TicTacToeLeaderboard(){

    }
    public static TicTacToeLeaderboard getInstance() {
        return instance;
    }
    @Override
    public List<Player> getScores() {
        sortLeaderboard();
        return new ArrayList<>(players);
    }

    public static void updatePlayer(Player player, boolean Won, int gameType){
        instance.addPlayer(player, gameType);
        instance.sortLeaderboard();
    }

    @Override
    public void displayLeaderboard() {
        PlayerDatabase.loadPlayersFromCSV();
        List<Player> TicTacToePlayers = new ArrayList<>();

        for(int currentID = 100000; currentID <= 999999; currentID++){
            Player player = PlayerDatabase.getPlayerByUserID(currentID);
            if(player != null && player.getMMR(gameType) > 0){
                TicTacToePlayers.add(player);
            }
        }

        //TicTacToePlayers.sort(p1,p2) -> Integer.compare(p2.getMMR(gameType), p1.getMMR(gameType)));
        this.players = TicTacToePlayers;
        sortLeaderboard();

        StringBuilder sb = new StringBuilder();
        sb.append("\n TicTacToe Leaderboard \n");
        sb.append(String.format("%-5s %-15s %-8s %-10s %-8s%n", "Position", "Username", "Level", "Rank", "MMR"));
        for (int i = 0; i < TicTacToePlayers.size(); i++) {
            Player p = TicTacToePlayers.get(i);
            sb.append(String.format("%-5d %-15s %-8d %-10s %-8d%n",
                    i+1,
                    p.getUsername(),
                    p.getLevel(),
                    p.getRank(gameType).getCurrentTier(),
                    p.getMMR(gameType)));
        }
    }

    public void sortLeaderboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getMMR(gameType), p1.getMMR(gameType)));
    }
}