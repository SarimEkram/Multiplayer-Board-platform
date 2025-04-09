package MatchmakingLeaderboard;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static MatchmakingLeaderboard.GameType.*;

public class PlayerTest {
    private Player player = new Player("TestPlayer", 1, 1001);;

    @Test
    void testConstructorInitialization() {
        assertEquals("TestPlayer", player.getUsername());
        assertEquals(1, player.getLevel());
        assertEquals(1001, player.getUserID());

        // Verify default values for each game type
        for (GameType gameType : GameType.values()) {
            assertEquals(0, player.getWins(gameType));
            assertEquals(0, player.getLosses(gameType));
            assertEquals(0.0, player.getWinRatio(gameType));
            assertEquals(0, player.getMMR(gameType));
            assertEquals(gameType.getGameCode(), player.getGameSignal(gameType));
            assertNotNull(player.getRank(gameType));
            assertEquals(RankTier.BRONZE, player.getRank(gameType).getCurrentTier());
        }
    }

    @Test
    void testWinLossOperations() {
        // Test for all game types
        for (GameType gameType : GameType.values()) {
            // Initial state
            assertEquals(0, player.getWins(gameType));
            assertEquals(0, player.getLosses(gameType));
            assertEquals(0.0, player.getWinRatio(gameType));

            // Add wins
            player.addWin(gameType);
            player.addWin(gameType);
            assertEquals(2, player.getWins(gameType));
            assertEquals(0, player.getLosses(gameType));
            assertEquals(1.0, player.getWinRatio(gameType));

            // Add losses
            player.addLoss(gameType);
            player.addLoss(gameType);
            player.addLoss(gameType);
            assertEquals(2, player.getWins(gameType));
            assertEquals(3, player.getLosses(gameType));
            assertEquals(0.4, player.getWinRatio(gameType));
        }
    }

    @Test
    void testWinRatioEdgeCases() {
        // Test division by zero protection
        assertEquals(0.0, player.getWinRatio(TIC_TAC_TOE));

        // Only losses
        player.addLoss(CONNECT_FOUR);
        player.addLoss(CONNECT_FOUR);
        assertEquals(0.0, player.getWinRatio(CONNECT_FOUR));

        // Only wins
        player.addWin(CHECKERS);
        player.addWin(CHECKERS);
        assertEquals(1.0, player.getWinRatio(CHECKERS));

        // Exactly 50% win rate
        player.addWin(TIC_TAC_TOE);
        player.addLoss(TIC_TAC_TOE);
        assertEquals(0.5, player.getWinRatio(TIC_TAC_TOE));
    }

    @Test
    void testMMROperations() {
        // Test setting and getting MMR for each game type
        player.setMMR(1200, TIC_TAC_TOE);
        assertEquals(1200, player.getMMR(TIC_TAC_TOE));

        player.setMMR(1500, CONNECT_FOUR);
        assertEquals(1500, player.getMMR(CONNECT_FOUR));

        player.setMMR(1800, CHECKERS);
        assertEquals(1800, player.getMMR(CHECKERS));

        // Test MMR independence between game types
        assertNotEquals(player.getMMR(TIC_TAC_TOE), player.getMMR(CHECKERS));
    }

    @Test
    void testGameSignalOperations() {
        // Test valid signal range
        for (int signal = 0; signal <= 3; signal++) {
            player.setGameSignal(signal, TIC_TAC_TOE);
            assertEquals(signal, player.getGameSignal(TIC_TAC_TOE));
        }

        // Test invalid signals
        assertThrows(IllegalArgumentException.class, () ->
                player.setGameSignal(-1, CONNECT_FOUR));
        assertThrows(IllegalArgumentException.class, () ->
                player.setGameSignal(4, CHECKERS));
    }

    @Test
    void testRankOperations() {
        // Test initial rank state
        Rank initialRank = player.getRank(TIC_TAC_TOE);
        assertEquals(0, initialRank.getRankingPoints());
        assertEquals(RankTier.BRONZE, initialRank.getCurrentTier());

        // Test rank adjustment to SILVER (1000 points)
        initialRank.adjustPoints(player, 1000, TIC_TAC_TOE);
        assertEquals(1000, player.getRank(TIC_TAC_TOE).getRankingPoints());
        assertEquals(RankTier.SILVER, player.getRank(TIC_TAC_TOE).getCurrentTier());

        // Test rank adjustment to GOLD (2000 points)
        initialRank.adjustPoints(player, 1000, TIC_TAC_TOE); // Adding another 1000 to reach 2000
        assertEquals(2000, player.getRank(TIC_TAC_TOE).getRankingPoints());
        assertEquals(RankTier.GOLD, player.getRank(TIC_TAC_TOE).getCurrentTier());

        // Test rank adjustment to DIAMOND (3000 points)
        initialRank.adjustPoints(player, 1000, TIC_TAC_TOE); // Adding another 1000 to reach 3000
        assertEquals(3000, player.getRank(TIC_TAC_TOE).getRankingPoints());
        assertEquals(RankTier.DIAMOND, player.getRank(TIC_TAC_TOE).getCurrentTier());

        // Test negative points adjustment (should clamp to 0)
        initialRank.adjustPoints(player, -4000, TIC_TAC_TOE);
        assertEquals(0, player.getRank(TIC_TAC_TOE).getRankingPoints());
        assertEquals(RankTier.BRONZE, player.getRank(TIC_TAC_TOE).getCurrentTier());

        // Test rank independence between game types
        assertEquals(0, player.getRank(CONNECT_FOUR).getRankingPoints());
    }

    @Test
    void testRankTierPromotion() {
        Rank rank = player.getRank(CHECKERS);

        // BRONZE to SILVER
        rank.adjustPoints(player, RankTier.SILVER.getThresholdPoints(), CHECKERS);
        assertEquals(RankTier.SILVER, rank.getCurrentTier());

        // SILVER to GOLD
        rank.adjustPoints(player, RankTier.GOLD.getThresholdPoints() - RankTier.SILVER.getThresholdPoints(), CHECKERS);
        assertEquals(RankTier.GOLD, rank.getCurrentTier());

        // GOLD to DIAMOND
        rank.adjustPoints(player, RankTier.DIAMOND.getThresholdPoints() - RankTier.GOLD.getThresholdPoints(), CHECKERS);
        assertEquals(RankTier.DIAMOND, rank.getCurrentTier());
    }

    @Test
    void testPlayerInformationUpdates() {
        // Test username
        player.setUsername("NewUsername");
        assertEquals("NewUsername", player.getUsername());

        // Test level
        player.setLevel(10);
        assertEquals(10, player.getLevel());

        // Test userID
        player.setUserID(2002);
        assertEquals(2002, player.getUserID());
    }

    @Test
    void testJoinMatchWithDifferentSignals() {
        // Test that joinMatch doesn't throw exceptions for valid signals
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

        // Signal 0 should do nothing
        assertDoesNotThrow(() -> {
            player.setGameSignal(0, TIC_TAC_TOE);
            player.joinMatch(TIC_TAC_TOE);
        });
    }

    @Test
    void testMultipleGameTypeIndependence() {
        // Verify that operations on one game type don't affect others
        player.addWin(TIC_TAC_TOE);
        player.setMMR(1500, TIC_TAC_TOE);
        player.getRank(TIC_TAC_TOE).adjustPoints(player, 100, TIC_TAC_TOE);

        assertEquals(0, player.getWins(CONNECT_FOUR));
        assertEquals(0, player.getMMR(CONNECT_FOUR));
        assertEquals(0, player.getRank(CONNECT_FOUR).getRankingPoints());
    }
}