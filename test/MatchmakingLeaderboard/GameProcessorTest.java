package MatchmakingLeaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameProcessorTest {

    private Player winner;
    private Player loser;
    private GameProcessor processor;

    @BeforeEach
    void setUp() {
        winner = new Player("Winner", 1, 111111);
        loser = new Player("Loser", 1, 222222);

        // Set rank for all game types since level depends on all MMRs now
        for (GameType gameType : GameType.values()) {
            winner.setRank(new Rank(), gameType);
            loser.setRank(new Rank(), gameType);
        }

        processor = new GameProcessor(winner, loser, GameType.TIC_TAC_TOE);
    }

    @Test
    void testConstructor_Valid() {
        assertEquals(winner, processor.getWinner());
        assertEquals(loser, processor.getLoser());
        assertEquals(GameType.TIC_TAC_TOE, processor.getType());
    }

    @Test
    void testConstructor_NullPlayer() {
        assertThrows(IllegalArgumentException.class, () -> new GameProcessor(null, loser, GameType.TIC_TAC_TOE));
        assertThrows(IllegalArgumentException.class, () -> new GameProcessor(winner, null, GameType.TIC_TAC_TOE));
    }

    @Test
    void testConstructor_NullGameType() {
        assertThrows(IllegalArgumentException.class, () -> new GameProcessor(winner, loser, null));
    }

    @Test
    void testUpdateResults_ChangesMMRAndStats() {
        int initialMMR = 1000;
        winner.setMMR(initialMMR, GameType.TIC_TAC_TOE);
        loser.setMMR(initialMMR, GameType.TIC_TAC_TOE);

        processor.UpdateResults(winner, loser, GameType.TIC_TAC_TOE);

        assertEquals(1, winner.getWins(GameType.TIC_TAC_TOE));
        assertEquals(1, loser.getLosses(GameType.TIC_TAC_TOE));
        assertNotEquals(initialMMR, winner.getMMR(GameType.TIC_TAC_TOE));
        assertNotEquals(initialMMR, loser.getMMR(GameType.TIC_TAC_TOE));
    }

    @Test
    void testUpdateMMR_WinVsLoss() {
        winner.setMMR(1000, GameType.TIC_TAC_TOE);
        loser.setMMR(1000, GameType.TIC_TAC_TOE);

        processor.updateMMR(winner, loser, true, GameType.TIC_TAC_TOE);
        processor.updateMMR(loser, winner, false, GameType.TIC_TAC_TOE);

        assertTrue(winner.getMMR(GameType.TIC_TAC_TOE) > 1000);
        assertTrue(loser.getMMR(GameType.TIC_TAC_TOE) < 1000);
    }

    @Test
    void testUpdateLevel_CorrectCalculation_AllGames() {
        // Setting MMRs for all game types
        winner.setMMR(50, GameType.TIC_TAC_TOE);       // contributes 0
        winner.setMMR(100, GameType.CONNECT_FOUR);     // contributes 1
        winner.setMMR(50, GameType.CHECKERS);          // contributes 0
        // Total = 200 / 100 = 2
        processor.updateLevel(winner);
        assertEquals(2, winner.getLevel());

        // Update MMRs again
        winner.setMMR(300, GameType.TIC_TAC_TOE);       // 3
        winner.setMMR(300, GameType.CONNECT_FOUR);      // 3
        winner.setMMR(300, GameType.CHECKERS);          // 3
        // Total = 900 / 100 = 9
        processor.updateLevel(winner);
        assertEquals(9, winner.getLevel());

        // Edge case: all MMRs are 0 → level should be 1
        winner.setMMR(0, GameType.TIC_TAC_TOE);
        winner.setMMR(0, GameType.CONNECT_FOUR);
        winner.setMMR(0, GameType.CHECKERS);
        processor.updateLevel(winner);
        assertEquals(1, winner.getLevel());
    }

    @Test
    void testProcessDraw_BothPlayersGainMMR() {
        winner.setMMR(1000, GameType.TIC_TAC_TOE);
        loser.setMMR(1000, GameType.TIC_TAC_TOE);

        processor.ProcessDraw(winner, loser, GameType.TIC_TAC_TOE);

        assertTrue(winner.getMMR(GameType.TIC_TAC_TOE) >= 1000);
        assertTrue(loser.getMMR(GameType.TIC_TAC_TOE) >= 1000);
    }
}
