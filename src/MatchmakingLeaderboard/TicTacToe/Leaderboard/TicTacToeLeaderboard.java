package MatchmakingLeaderboard.TicTacToe.Leaderboard;

import MatchmakingLeaderboard.Player;
import MatchmakingLeaderboard.PlayerDatabase;
import MatchmakingLeaderboard.GameType;
import static MatchmakingLeaderboard.GameType.TIC_TAC_TOE;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeLeaderboard extends AbstractTicTacToeLeaderboard {

    private static final TicTacToeLeaderboard instance = new TicTacToeLeaderboard();

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private TicTacToeLeaderboard() {
    }

    /**
     * Provides access to the Singleton instance of the Checkers leaderboard.
     */
    public static TicTacToeLeaderboard getInstance() {
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

        List<Player> ticTacToePlayers = new ArrayList<>();
        for (Player player : allPlayers) {
            if (player.getMMR(TIC_TAC_TOE) >= 0) {
                ticTacToePlayers.add(player);
            }
        }

        this.players = ticTacToePlayers;
        sortLeaderboard();
    }

    /**
     * Sorts the leaderboard in descending order of players' Checkers MMR.
     */
    public void sortLeaderboard() {
        players.sort((p1, p2) -> Integer.compare(p2.getMMR(TIC_TAC_TOE), p1.getMMR(TIC_TAC_TOE)));
    }
}
