package MatchmakingLeaderboard;

import MatchmakingLeaderboard.Checkers.Matchmaking.CheckersMatchmaking;
import MatchmakingLeaderboard.Connect4.Matchmaking.Connect4Matchmaking;
import MatchmakingLeaderboard.TicTacToe.Matchmaking.TicTacToeMatchmaking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;

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

        for (int i = 1; i <= 3; i++) {
            assertEquals(0, player.getWins(i));
            assertEquals(0, player.getLosses(i));
            assertEquals(0.0, player.getWinRatio(i));
            assertEquals(0, player.getMMR(i));
            assertEquals(0, player.getGameSignal(i));
            assertNotNull(player.getRank(i));
        }
    }

    /**
     * Tests win addition and win ratio calculation for game type 1 (TicTacToe)
     */
    @Test
    void testAddWinAndCalculateRatio() {
        player.addWin(1); // TicTacToe
        assertEquals(1, player.getWins(1));
        assertEquals(0, player.getLosses(1));
        assertEquals(1.0, player.getWinRatio(1));

        player.addLoss(1);
        assertEquals(1, player.getWins(1));
        assertEquals(1, player.getLosses(1));
        assertEquals(0.5, player.getWinRatio(1));
    }

    /**
     * Tests loss addition and win ratio calculation for game type 2 (Connect4)
     */
    @Test
    void testAddLossAndCalculateRatio() {
        player.addLoss(2); // Connect4
        assertEquals(0, player.getWins(2));
        assertEquals(1, player.getLosses(2));
        assertEquals(0.0, player.getWinRatio(2));

        player.addWin(2);
        player.addWin(2);
        assertEquals(2, player.getWins(2));
        assertEquals(1, player.getLosses(2));
        assertEquals(2.0/3, player.getWinRatio(2));
    }

    /**
     * Tests MMR setting/getting for game type 3 (Checkers)
      */
    @Test
    void testSetMMR() {
        player.setMMR(1500, 3); // Checkers
        assertEquals(1500, player.getMMR(3));
    }

    /**
     * Tests game signal
     */
    @Test
    void testSetGameSignal() {
        player.setGameSignal(2, 1); // TicTacToe signal 2
        assertEquals(2, player.getGameSignal(1));

        assertThrows(IllegalArgumentException.class, () -> player.setGameSignal(4, 1));
    }

    /**
     * Tests validation of game type parameters
     */
    @Test
    void testInvalidGameType() {
        assertThrows(IllegalArgumentException.class, () -> player.getWins(0));
        assertThrows(IllegalArgumentException.class, () -> player.getWins(4));
        assertThrows(IllegalArgumentException.class, () -> player.setMMR(1000, 0));
    }

    /**
     * Tests rank assignment and point adjustment
     */
    @Test
    void testSetAndGetRank() {
        Rank newRank = new Rank();
        player.setRank(newRank, 2);
        newRank.adjustPoints(player, 100, 2);
        assertEquals(100, player.getRank(2).getRankingPoints());
    }


//    @Test
//    void testJoinMatch() throws Exception {
//        // Using mocks to test joinMatch without actual implementations
//        TicTacToeMatchmaking mockTicTacToe = mock(TicTacToeMatchmaking.class);
//        Connect4Matchmaking mockConnect4 = mock(Connect4Matchmaking.class);
//        CheckersMatchmaking mockCheckers = mock(CheckersMatchmaking.class);
//
//        player.setGameSignal(1, 1);
//        assertEquals(1, player.getGameSignal(1));
//    }

    /**
     * test of other basic setters
     */
    @Test
    void testSetters() {
        player.setLevel(10);
        assertEquals(10, player.getLevel());

        player.setUserID(123456);
        assertEquals(123456, player.getUserID());
    }


}
