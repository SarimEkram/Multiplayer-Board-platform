package networking.game;

import networking.game.TurnTimer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class  TurnTimerTest {
    private TurnTimer timer;

    @BeforeEach
    void setUp() {
        timer = new TurnTimer("player1", 2);
    }

    @Test
    void testStartTimer() {
        timer.startTimer();
        assertFalse(timer.isTimeExpired());
    }

    @Test
    void testPauseTimer() throws InterruptedException {
        timer.startTimer();
        Thread.sleep(1000);
        timer.pauseTimer();

        Thread.sleep(1000);
        timer.resumeTimer();

        assertFalse(timer.isTimeExpired());
    }

    @Test
    void testResumeTimer() throws InterruptedException {
        timer.startTimer();
        Thread.sleep(1000);
        timer.pauseTimer();

        long beforeResume = System.currentTimeMillis();
        Thread.sleep(500);
        timer.resumeTimer();


        Thread.sleep(800);
        assertFalse(timer.isTimeExpired());
    }

    @Test
    void testResetTimer() throws InterruptedException {
        timer.startTimer();
        Thread.sleep(1500);
        timer.resetTimer();
        assertFalse(timer.isTimeExpired());
    }

    @Test
    void testTimeExpired() throws InterruptedException {
        timer.startTimer();
        Thread.sleep(2200);
        assertTrue(timer.isTimeExpired());
    }

    @Test
    void testHandleTimerExpiry() throws InterruptedException {
        timer.startTimer();
        Thread.sleep(2200);
        timer.handleTimerExpiry();
        assertTrue(timer.isTimeExpired());
    }

    @Test
    void testNotifyPlayer() throws InterruptedException {
        TurnTimer shortTimer = new TurnTimer("player2", 1);
        shortTimer.startTimer();
        Thread.sleep(100);
        shortTimer.notifyPlayer();

        Thread.sleep(950);
        shortTimer.notifyPlayer();
    }
    @Test
    void testPauseWhenAlreadyPaused() throws InterruptedException {
        timer.startTimer();
        Thread.sleep(500);
        timer.pauseTimer();
        long pausedTime = timer.getRemainingTime();
        timer.pauseTimer();
        assertEquals(pausedTime, timer.getRemainingTime(), 50);
    }

    @Test
    void testResumeWhenNotPaused() {
        timer.startTimer();
        timer.resumeTimer();
        assertFalse(timer.isTimeExpired());
    }
    @Test
    void testNotifyPlayerNoWarning() throws InterruptedException {
        TurnTimer longTimer = new TurnTimer("player3", 20);
        longTimer.startTimer();
        Thread.sleep(500);
        longTimer.notifyPlayer();
    }




}
