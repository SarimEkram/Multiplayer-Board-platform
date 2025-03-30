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
        List<Player> allPlayers = new ArrayList<>(players);
        StringBuilder sb = new StringBuilder();
        sb.append("\n Checkers Leaderboard \n");
        //sb.append(String.format())
    }



    public void sortLeaderboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getMMR(gameType), p1.getMMR(gameType)));
    }
}
