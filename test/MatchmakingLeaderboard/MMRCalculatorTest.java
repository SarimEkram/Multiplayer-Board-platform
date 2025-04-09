package MatchmakingLeaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MMRCalculatorTest {

    private Player p1;
    private Player p2;

    @BeforeEach
    void setUp() {
        // Create two test players before each test
        p1 = new Player("Player1", 5, 101);
        p2 = new Player("Player2", 5, 202);
    }

    @Test
    void testCalculateMMR_HighBeatsLow() {
        // p1 has high win ratio (8W, 2L = 0.8)
        for (int i = 0; i < 8; i++) p1.addWin(GameType.TIC_TAC_TOE);
        for (int i = 0; i < 2; i++) p1.addLoss(GameType.TIC_TAC_TOE);

        // p2 has low win ratio (1W, 9L = 0.1)
        p2.addWin(GameType.TIC_TAC_TOE);
        for (int i = 0; i < 9; i++) p2.addLoss(GameType.TIC_TAC_TOE);

        // Skill difference = 0.8 - 0.1 = 0.7 → modifier = 1 + (0.7 * 0.5) = 1.35
        // Expected MMR gain = 25 * 1.35 = 33.75 → int = 33
        int expected = 33;
        int actual = MMRCalculator.calculateMMR(p1, p2, true, GameType.TIC_TAC_TOE);
        assertEquals(expected, actual);
    }

    @Test
    void testCalculateMMR_LowBeatsHigh() {
        // p1 = low win ratio (2W, 8L = 0.2)
        for (int i = 0; i < 2; i++) p1.addWin(GameType.TIC_TAC_TOE);
        for (int i = 0; i < 8; i++) p1.addLoss(GameType.TIC_TAC_TOE);

        // p2 = high win ratio (9W, 1L = 0.9)
        for (int i = 0; i < 9; i++) p2.addWin(GameType.TIC_TAC_TOE);
        p2.addLoss(GameType.TIC_TAC_TOE);

        // Skill difference = 0.2 - 0.9 = -0.7 → modifier = 1 + (-0.7 * 0.5) = 0.65
        // Expected MMR gain = 25 * 0.65 = 16.25 → int = 16
        int expected = 16;
        int actual = MMRCalculator.calculateMMR(p1, p2, true, GameType.TIC_TAC_TOE);
        assertEquals(expected, actual);
    }

    @Test
    void testCalculateMMR_LossFromHigh() {
        // p1 = high win ratio (9W, 1L = 0.9)
        for (int i = 0; i < 9; i++) p1.addWin(GameType.TIC_TAC_TOE);
        p1.addLoss(GameType.TIC_TAC_TOE);

        // p2 = low win ratio (2W, 8L = 0.2)
        for (int i = 0; i < 2; i++) p2.addWin(GameType.TIC_TAC_TOE);
        for (int i = 0; i < 8; i++) p2.addLoss(GameType.TIC_TAC_TOE);

        // Skill diff = 0.9 - 0.2 = 0.7 → modifier = 1.35
        // Expected MMR loss = -12.5 * 1.35 = -16.875 → int = -16
        int expected = -16;
        int actual = MMRCalculator.calculateMMR(p1, p2, false, GameType.TIC_TAC_TOE);
        assertEquals(expected, actual);
    }

    @Test
    void testCalculateDraw_SimilarSkills() {
        // Both players have equal skill: 5W, 5L = 0.5 win ratio
        for (int i = 0; i < 5; i++) {
            p1.addWin(GameType.TIC_TAC_TOE);
            p1.addLoss(GameType.TIC_TAC_TOE);
            p2.addWin(GameType.TIC_TAC_TOE);
            p2.addLoss(GameType.TIC_TAC_TOE);
        }

        // Skill diff = 0 → modifier = 1 → 2 * 1 = 2
        int expected = 2;
        int actual = MMRCalculator.calculateDraw(p1, p2, GameType.TIC_TAC_TOE);
        assertEquals(expected, actual);
    }

    @Test
    void testCalculateDraw_DifferentSkills() {
        // p1 = 9W, 1L = 0.9
        for (int i = 0; i < 9; i++) p1.addWin(GameType.TIC_TAC_TOE);
        p1.addLoss(GameType.TIC_TAC_TOE);

        // p2 = 1W, 9L = 0.1
        p2.addWin(GameType.TIC_TAC_TOE);
        for (int i = 0; i < 9; i++) p2.addLoss(GameType.TIC_TAC_TOE);

        // Skill diff = |0.9 - 0.1| = 0.8 → modifier = 0.2 (clamped minimum)
        // Expected MMR gain = 2 * 0.2 = 0.4 → int = 0
        int expected = 0;
        int actual = MMRCalculator.calculateDraw(p1, p2, GameType.TIC_TAC_TOE);
        assertEquals(expected, actual);
    }
}
