package networking.game;

import networking.game.TurnTimer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class  TurnTimerTest {
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

    @Test
    void testPauseTimer() throws InterruptedException {
        timer.startTimer();
        Thread.sleep(1000); // wait 1 second
        timer.pauseTimer();

        Thread.sleep(1000); // this should not count because it's paused
        timer.resumeTimer();

        assertFalse(timer.isTimeExpired()); // should still have about 1 sec left
    }

    @Test
    void testResumeTimer() throws InterruptedException {
        timer.startTimer();
        Thread.sleep(1000);
        timer.pauseTimer();

        long beforeResume = System.currentTimeMillis();
        Thread.sleep(500); // wait while paused
        timer.resumeTimer();

        // check that we're still not expired
        Thread.sleep(800); // continue ticking
        assertFalse(timer.isTimeExpired()); // should still not be expired
    }

    @Test
    void testResetTimer() throws InterruptedException {
        timer.startTimer();
        Thread.sleep(1500);
        timer.resetTimer(); // reset mid-way
        assertFalse(timer.isTimeExpired());
    }

    @Test
    void testTimeExpired() throws InterruptedException {
        timer.startTimer();
        Thread.sleep(2200); // longer than 2 seconds
        assertTrue(timer.isTimeExpired());
    }

    @Test
    void testHandleTimerExpiry() throws InterruptedException {
        timer.startTimer();
        Thread.sleep(2200);
        timer.handleTimerExpiry(); // prints disconnect message if expired
        assertTrue(timer.isTimeExpired());
    }

    @Test
    void testNotifyPlayer() throws InterruptedException {
        TurnTimer shortTimer = new TurnTimer("player2", 1); // 1 second duration
        shortTimer.startTimer();
        Thread.sleep(100); // simulate short delay
        shortTimer.notifyPlayer(); // Should NOT notify yet

        Thread.sleep(950); // total wait ~1.05s
        shortTimer.notifyPlayer(); // Should trigger warning
    }

}
