package networking.game.tests;

import networking.game.TurnTimer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TurnTimerTest {
    private TurnTimer timer;

    @BeforeEach
    void setUp() {
        timer = new TurnTimer("player1", 2); // 2 seconds
    }

    @Test
    void testStartTimer() {
        timer.startTimer();
        assertFalse(timer.isTimeExpired()); // Should not be expired immediately
    }

}
