package MatchmakingLeaderboard.Checkers.Leaderboard;

import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import MatchmakingLeaderboard.GameType;

import java.util.ArrayList;
import java.util.List;

import static MatchmakingLeaderboard.GameType.CHECKERS;

/**
 * Concrete class for Checkers leaderboard management.
 */
public class CheckersLeaderboard extends AbstractCheckersLeaderboard {

    private static final CheckersLeaderboard instance = new CheckersLeaderboard();

    /**
     * Constructor
     */
    private CheckersLeaderboard() {
    }

    /**
     * Private constructor to enforce Singleton pattern.
     */
    public static CheckersLeaderboard getInstance() {
        return instance;
    }

    /**
     * @return array list of players
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
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
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

        List<Player> checkersPlayers = new ArrayList<>();
        for (Player player : allPlayers) {
            if (player.getMMR(CHECKERS) >= 0) {
                checkersPlayers.add(player);
            }
        }

        this.players = checkersPlayers;
        sortLeaderboard();
    }


    /**
     * Sorts the leaderboard in descending order of players' Checkers MMR.
     */
    @Override
    public void sortLeaderboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getMMR(CHECKERS), p1.getMMR(CHECKERS)));
    }
}
