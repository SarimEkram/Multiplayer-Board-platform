package MatchmakingLeaderboard;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static MatchmakingLeaderboard.GameType.*;

public class PlayerTest {
    private Player player;



    @Test
    void testConstructor() {
        player = new Player("Ramesh", 5, 123456);
        assertEquals("Ramesh", player.getUsername());
        assertEquals(5, player.getLevel());
        assertEquals(123456, player.getUserID());

        // Test defaults for each game type
        assertEquals(0, player.getWins(TIC_TAC_TOE));
        assertEquals(0, player.getLosses(TIC_TAC_TOE));
        assertEquals(0.0, player.getWinRatio(TIC_TAC_TOE));
        assertEquals(0, player.getMMR(TIC_TAC_TOE));
        assertEquals(TIC_TAC_TOE.getGameCode(), player.getGameSignal(TIC_TAC_TOE));
        assertNotNull(player.getRank(TIC_TAC_TOE));

        assertEquals(0, player.getWins(CONNECT_FOUR));
        assertEquals(0, player.getLosses(CONNECT_FOUR));
        assertEquals(0.0, player.getWinRatio(CONNECT_FOUR));
        assertEquals(0, player.getMMR(CONNECT_FOUR));
        assertEquals(CONNECT_FOUR.getGameCode(), player.getGameSignal(CONNECT_FOUR));
        assertNotNull(player.getRank(CONNECT_FOUR));

        assertEquals(0, player.getWins(CHECKERS));
        assertEquals(0, player.getLosses(CHECKERS));
        assertEquals(0.0, player.getWinRatio(CHECKERS));
        assertEquals(0, player.getMMR(CHECKERS));
        assertEquals(CHECKERS.getGameCode(), player.getGameSignal(CHECKERS));
        assertNotNull(player.getRank(CHECKERS));
    }

    @Test
    void testWinLossOperationsForTicTacToe() {
        // Initial state
        assertEquals(0, player.getWins(TIC_TAC_TOE));
        assertEquals(0, player.getLosses(TIC_TAC_TOE));
        assertEquals(0.0, player.getWinRatio(TIC_TAC_TOE));

        // Add wins
        player.addWin(TIC_TAC_TOE);
        player.addWin(TIC_TAC_TOE);
        assertEquals(2, player.getWins(TIC_TAC_TOE));
        assertEquals(0, player.getLosses(TIC_TAC_TOE));
        assertEquals(1.0, player.getWinRatio(TIC_TAC_TOE));

        // Add losses
        player.addLoss(TIC_TAC_TOE);
        player.addLoss(TIC_TAC_TOE);
        player.addLoss(TIC_TAC_TOE);
        assertEquals(2, player.getWins(TIC_TAC_TOE));
        assertEquals(3, player.getLosses(TIC_TAC_TOE));
        assertEquals(0.4, player.getWinRatio(TIC_TAC_TOE));
    }

    @Test
    void testWinLossOperationsForConnectFour() {
        // Similar tests for Connect Four
        player.addWin(CONNECT_FOUR);
        assertEquals(1, player.getWins(CONNECT_FOUR));
        assertEquals(0, player.getLosses(CONNECT_FOUR));
        assertEquals(1.0, player.getWinRatio(CONNECT_FOUR));

        player.addLoss(CONNECT_FOUR);
        assertEquals(1, player.getWins(CONNECT_FOUR));
        assertEquals(1, player.getLosses(CONNECT_FOUR));
        assertEquals(0.5, player.getWinRatio(CONNECT_FOUR));
    }

    @Test
    void testWinLossOperationsForCheckers() {
        // Similar tests for Checkers
        player.addLoss(CHECKERS);
        assertEquals(0, player.getWins(CHECKERS));
        assertEquals(1, player.getLosses(CHECKERS));
        assertEquals(0.0, player.getWinRatio(CHECKERS));

        player.addWin(CHECKERS);
        player.addWin(CHECKERS);
        assertEquals(2, player.getWins(CHECKERS));
        assertEquals(1, player.getLosses(CHECKERS));
        assertEquals(2.0/3, player.getWinRatio(CHECKERS));
    }

    @Test
    void testWinRatioEdgeCases() {
        // No games played
        assertEquals(0.0, player.getWinRatio(TIC_TAC_TOE));

        // Only losses
        player.addLoss(TIC_TAC_TOE);
        player.addLoss(TIC_TAC_TOE);
        assertEquals(0.0, player.getWinRatio(TIC_TAC_TOE));

        // Only wins
        Player winningPlayer = new Player("Champion", 10, 999);
        winningPlayer.addWin(CONNECT_FOUR);
        winningPlayer.addWin(CONNECT_FOUR);
        assertEquals(1.0, winningPlayer.getWinRatio(CONNECT_FOUR));
    }

    @Test
    void testMMROperations() {
        // Test for each game type
        player.setMMR(1500, TIC_TAC_TOE);
        assertEquals(1500, player.getMMR(TIC_TAC_TOE));

        player.setMMR(1600, CONNECT_FOUR);
        assertEquals(1600, player.getMMR(CONNECT_FOUR));

        player.setMMR(1700, CHECKERS);
        assertEquals(1700, player.getMMR(CHECKERS));
    }

    @Test
    void testGameSignalOperations() {
        // Test valid signals
        player.setGameSignal(0, TIC_TAC_TOE);
        assertEquals(0, player.getGameSignal(TIC_TAC_TOE));

        player.setGameSignal(1, CONNECT_FOUR);
        assertEquals(1, player.getGameSignal(CONNECT_FOUR));

        player.setGameSignal(2, CHECKERS);
        assertEquals(2, player.getGameSignal(CHECKERS));



        // Test invalid signals
        assertThrows(IllegalArgumentException.class, () ->
                player.setGameSignal(-1, TIC_TAC_TOE));
        assertThrows(IllegalArgumentException.class, () ->
                player.setGameSignal(4, TIC_TAC_TOE));
    }

    @Test
    void testRankOperations() {
        // Create a new player and get their initial CONNECT_FOUR rank
        Rank initialRank = player.getRank(CONNECT_FOUR);
        assertEquals(0, initialRank.getRankingPoints());

        // Adjust points for the player's CONNECT_FOUR rank
        initialRank.adjustPoints(player, 100, CONNECT_FOUR);

        // Verify the player's CONNECT_FOUR rank was updated
        assertEquals(100, player.getRank(CONNECT_FOUR).getRankingPoints());

        // Create a new rank and set it for CONNECT_FOUR
        Rank newRank = new Rank(RankTier.BRONZE);
        player.setRank(newRank, CONNECT_FOUR);

        // Verify the new rank was set correctly
        assertEquals(newRank, player.getRank(CONNECT_FOUR));
        assertEquals(200, player.getRank(CONNECT_FOUR).getRankingPoints());

        // Verify rank is game-specific
        assertNotEquals(newRank, player.getRank(TIC_TAC_TOE));
        assertEquals(0, player.getRank(TIC_TAC_TOE).getRankingPoints());
    }

    @Test
    void testBasicSettersAndGetters() {
        // Level
        player.setLevel(10);
        assertEquals(10, player.getLevel());

        // UserID
        player.setUserID(654321);
        assertEquals(654321, player.getUserID());

        // Username should be immutable (no setter)
        assertEquals("Ramesh", player.getUsername());
    }

    @Test
    void testJoinMatch() {
        // This tests that the method doesn't throw exceptions
        // Without Mockito, we can't verify the actual matchmaking behavior
        assertDoesNotThrow(() -> {
            player.setGameSignal(1, TIC_TAC_TOE);
            player.joinMatch(TIC_TAC_TOE);
        });

        assertDoesNotThrow(() -> {
            player.setGameSignal(2, CONNECT_FOUR);
            player.joinMatch(CONNECT_FOUR);
        });

        assertDoesNotThrow(() -> {
            player.setGameSignal(3, CHECKERS);
            player.joinMatch(CHECKERS);
        });
    }

    @Test
    void testJoinMatchWithInvalidSignal() {
        player.setGameSignal(0, TIC_TAC_TOE);
        assertDoesNotThrow(() -> player.joinMatch(TIC_TAC_TOE));

        assertThrows(IllegalArgumentException.class, () -> {
            player.setGameSignal(4, CONNECT_FOUR);
            player.joinMatch(CONNECT_FOUR);
        });
    }
}