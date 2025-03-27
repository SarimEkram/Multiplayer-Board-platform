package MatchmakingLeaderboard.Checkers.Leaderboard;


import MatchmakingLeaderboard.Player;

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

    @Override
    public List<Player> getScores() {
        sortLeaderboard();
        return players;
    }

    @Override
    public static void updatePlayer(Player player, boolean Won){

    }

    @Override
    public List<Player> getTopPlayers() {
        return List.of();
    }

    @Override
    public void displayLeaderboard() {

    }

    private Player findPlayerById(int playerId) {
        return null;
    }

    public void sortLeaderboard() {}
}
