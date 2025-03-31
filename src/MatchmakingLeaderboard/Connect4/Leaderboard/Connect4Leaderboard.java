package MatchmakingLeaderboard.Connect4.Leaderboard;

import MatchmakingLeaderboard.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Concrete class for Connect-4 leaderboard management.
 * @author Rahnuha Nurain
 * @author Neel Savani
 */
public class Connect4Leaderboard extends AbstractConnect4Leaderboard{

    private static final Connect4Leaderboard instance = new Connect4Leaderboard();

    private Connect4Leaderboard(){

    }
    public static Connect4Leaderboard getInstance() {
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
        List<Player> Connect4Players = new ArrayList<>();

        for(int currentID = 100000; currentID <= 999999; currentID++){
            Player player = PlayerDatabase.getPlayerByUserID(currentID);
            if(player != null && player.getMMR(gameType) > 0){
                Connect4Players.add(player);
            }
        }

        //Connect4Players.sort(p1,p2) -> Integer.compare(p2.getMMR(gameType), p1.getMMR(gameType)));
        this.players = Connect4Players;
        sortLeaderboard();

        StringBuilder sb = new StringBuilder();
        sb.append("\n Connect4 Leaderboard \n");
        sb.append(String.format("%-5s %-15s %-8s %-10s %-8s%n", "Rank", "Username", "Level", "Rank", "MMR"));
        for (int i = 0; i < Connect4Players.size(); i++) {
            Player p = Connect4Players.get(i);
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
