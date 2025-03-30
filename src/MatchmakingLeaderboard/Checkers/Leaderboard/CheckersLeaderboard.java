package MatchmakingLeaderboard.Checkers.Leaderboard;


import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;

import java.util.*;
/**
 * Concrete class for Checkers leaderboard management.
 * Extends Abstract Checkers Leaderboard
 *
 * @author Jay Thakor
 * @author Neel Savani
 * @author Happy Prajapati
 */

public class CheckersLeaderboard extends AbstractCheckersLeaderboard{

    private static final CheckersLeaderboard instance = new CheckersLeaderboard();

    private CheckersLeaderboard(){

    }
    public static CheckersLeaderboard getInstance() {
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
        List<Player> CheckersPlayers = new ArrayList<>();

        for(int currentID = 100000; currentID <= 999999; currentID++){
            Player player = PlayerDatabase.getPlayerByUserID(currentID);
            if(player != null && player.getMMR(gameType) > 0){
                CheckersPlayers.add(player);
            }
        }

        //CheckersPlayers.sort(p1,p2) -> Integer.compare(p2.getMMR(gameType), p1.getMMR(gameType)));
        this.players = CheckersPlayers;
        sortLeaderboard();

        StringBuilder sb = new StringBuilder();
        sb.append("\n Checkers Leaderboard \n");
        sb.append(String.format("%-5s %-15s %-8s %-10s %-8s%n", "Rank", "Username", "Level", "Rank", "MMR"));
        for (int i = 0; i < CheckersPlayers.size(); i++) {
            Player p = CheckersPlayers.get(i);
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
