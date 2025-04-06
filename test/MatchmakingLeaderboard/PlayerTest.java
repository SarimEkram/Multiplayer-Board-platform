package MatchmakingLeaderboard;

import MatchmakingLeaderboard.Checkers.Matchmaking.CheckersMatchmaking;
import MatchmakingLeaderboard.Connect4.Matchmaking.Connect4Matchmaking;
import MatchmakingLeaderboard.TicTacToe.Matchmaking.TicTacToeMatchmaking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static MatchmakingLeaderboard.GameType.*;

public class PlayerTest {
    private Player player;

    /**
     * Initializes a fresh Player instance before each test method executes
     */
    @BeforeEach
    void setUp() {
        player = new Player("Ramesh", 5, 123456);
    }

    /**
     * Tests the Player constructor, initializes all fields with correct default values
     */
    @Test
    void testConstructor() {
        assertEquals("Ramesh", player.getUsername());
        assertEquals(5, player.getLevel());
        assertEquals(123456, player.getUserID());

        for (GameType gameType : GameType.values()) {
            assertEquals(0, player.getWins(gameType));
            assertEquals(0, player.getLosses(gameType));
            assertEquals(0.0, player.getWinRatio(gameType));
            assertEquals(0, player.getMMR(gameType));
            assertEquals(gameType.getGameCode(), player.getGameSignal(gameType));
            assertNotNull(player.getRank(gameType));
        }
    }

    /**
     * Tests win addition and win ratio calculation for TIC_TAC_TOE
     */
    @Test
    void testAddWinAndCalculateRatio() {
        player.addWin(TIC_TAC_TOE);
        assertEquals(1, player.getWins(TIC_TAC_TOE));
        assertEquals(0, player.getLosses(TIC_TAC_TOE));
        assertEquals(1.0, player.getWinRatio(TIC_TAC_TOE));

        player.addLoss(TIC_TAC_TOE);
        assertEquals(1, player.getWins(TIC_TAC_TOE));
        assertEquals(1, player.getLosses(TIC_TAC_TOE));
        assertEquals(0.5, player.getWinRatio(TIC_TAC_TOE));
    }

    /**
     * Tests loss addition and win ratio calculation for CONNECT_FOUR
     */
    @Test
    void testAddLossAndCalculateRatio() {
        player.addLoss(CONNECT_FOUR);
        assertEquals(0, player.getWins(CONNECT_FOUR));
        assertEquals(1, player.getLosses(CONNECT_FOUR));
        assertEquals(0.0, player.getWinRatio(CONNECT_FOUR));

        player.addWin(CONNECT_FOUR);
        player.addWin(CONNECT_FOUR);
        assertEquals(2, player.getWins(CONNECT_FOUR));
        assertEquals(1, player.getLosses(CONNECT_FOUR));
        assertEquals(2.0/3, player.getWinRatio(CONNECT_FOUR));
    }

    /**
     * Tests MMR setting/getting for CHECKERS
     */
    @Test
    void testSetMMR() {
        player.setMMR(1500, CHECKERS);
        assertEquals(1500, player.getMMR(CHECKERS));
    }

    /**
     * Tests game signal
     */
    @Test
    void testSetGameSignal() {
        player.setGameSignal(2, TIC_TAC_TOE);
        assertEquals(2, player.getGameSignal(TIC_TAC_TOE));

        assertThrows(IllegalArgumentException.class, () ->
                player.setGameSignal(4, TIC_TAC_TOE));
    }

    /**
     * Tests validation of game type parameters
     */
    @Test
    void testInvalidGameType() {
        // Testing with invalid game codes (though now impossible with enum type)
        // These tests might be removed since compiler prevents invalid types
        assertThrows(IllegalArgumentException.class, () ->
                player.getWins(null));
    }

    /**
     * Tests rank assignment and point adjustment
     */
    @Test
    void testSetAndGetRank() {
        Rank newRank = new Rank();
        player.setRank(newRank, CONNECT_FOUR);
        newRank.adjustPoints(player, 100, CONNECT_FOUR);
        assertEquals(100, player.getRank(CONNECT_FOUR).getRankingPoints());
    }

    /**
     * Test of other basic setters
     */
    @Test
    void testSetters() {
        player.setLevel(10);
        assertEquals(10, player.getLevel());

        player.setUserID(654321);
        assertEquals(654321, player.getUserID());
    }

    /**
     * Tests joinMatch functionality (without actual implementations)
     */
    @Test
    void testJoinMatch() {
        // This would ideally use mocks as in your commented code
        assertDoesNotThrow(() -> {
            player.setGameSignal(1, TIC_TAC_TOE);
            player.joinMatch(TIC_TAC_TOE);
        });
    }
}