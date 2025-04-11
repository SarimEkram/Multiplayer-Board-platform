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

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private Connect4Leaderboard() {
    }

    /**
     * Provides access to the Singleton instance of the Checkers leaderboard.
     * @return The single instance of CheckersLeaderboard
     */
    public static Connect4Leaderboard getInstance() {
        return instance;
    }

    /**
     * Retrieves the current leaderboard scores.
     */
    @Override
    public List<Player> getScores() {
        sortLeaderboard();
        return new ArrayList<>(players);
    }

    /**
     * Updates a player's standing in the leaderboard.
     * Adds the player to the leaderboard and triggers a re-sort.
     * @param player
     * @param Won
     * @param gameType
     */
    public static void updatePlayer(Player player, boolean Won, GameType gameType) {
        instance.addPlayer(player, gameType);
        instance.sortLeaderboard();
    }

    /**
     * Displays the current leaderboard by loading player data from CSV,
     * filtering for Checkers players, and sorting by MMR.
     */
    @Override
    public void displayLeaderboard() {
        PlayerDatabase.loadPlayersFromCSV();
        List<Player> allPlayers = PlayerDatabase.getAllPlayers();

        List<Player> connect4Players = new ArrayList<>();
        for (Player player : allPlayers) {
            if (player.getMMR(CONNECT_FOUR) >= 0) {
                connect4Players.add(player);
            }
        }

        this.players = connect4Players;
        sortLeaderboard();
    }

    /**
     * Sorts the leaderboard in descending order of players' Checkers MMR.
     */
    @Override
    public void sortLeaderboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getMMR(CONNECT_FOUR), p1.getMMR(CONNECT_FOUR)));
    }
}
