package networking.game.tests;

import networking.game.TurnTimer;
import org.junit.jupiter.api.*;

public class TurnTimerTest {
    private TurnTimer timer;

    @BeforeEach
    void setUp() {
        timer = new TurnTimer("player1", 2); // 2 seconds
    }

}
