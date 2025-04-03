package networking.reconnection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import java.util.HashSet;
import java.util.Set;

/**
 * Dummy GameState class for testing.
 */
class GameState {
    // Empty base class; override toString() in subclass as needed.
}

/**
 * Test implementation of GameState.
 */
class TestGameState extends GameState {
    private final String state;

    public TestGameState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }
}

public class ReconnectionManagerTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        outContent.reset();
    }

    // --- Tests for startGame ---

    @Test
    public void testStartGameWithPlayers() {
        ReconnectionManager manager = new ReconnectionManager("game1");
        Set<String> players = new HashSet<>(Arrays.asList("p1", "p2", "p3"));
        manager.startGame(players);
        String output = outContent.toString().trim();
        assertTrue(output.contains("Game started with players:"));
        assertTrue(output.contains("p1"));
        assertTrue(output.contains("p2"));
        assertTrue(output.contains("p3"));
    }

    @Test
    public void testStartGameWithEmptyPlayers() {
        ReconnectionManager manager = new ReconnectionManager("game2");
        Set<String> players = new HashSet<>();
        manager.startGame(players);
        String output = outContent.toString().trim();
        // Depending on HashSet's toString, it should display an empty collection.
        assertTrue(output.contains("Game started with players:"));
        assertTrue(output.contains("[]"));
    }

    // --- Tests for saveGameState ---
    @Test
    public void testSaveGameState_NewState() {
        ReconnectionManager manager = new ReconnectionManager("game3");
        TestGameState state1 = new TestGameState("state1");
        manager.saveGameState("p1", state1);
        String output = outContent.toString().trim();
        assertTrue(output.contains("Saved game state for player p1: state1"));
    }

    @Test
    public void testSaveGameState_UpdateState() {
        ReconnectionManager manager = new ReconnectionManager("game4");
        TestGameState state1 = new TestGameState("state1");
        TestGameState state2 = new TestGameState("state2");
        manager.saveGameState("p1", state1);
        outContent.reset();
        manager.saveGameState("p1", state2);
        String output = outContent.toString().trim();
        assertTrue(output.contains("Saved game state for player p1: state2"));
    }
    @Test
    public void testSaveGameState_NewPlayer() {
        ReconnectionManager manager = new ReconnectionManager("game5");
        TestGameState state3 = new TestGameState("state3");
        manager.saveGameState("p2", state3);
        String output = outContent.toString().trim();
        assertTrue(output.contains("Saved game state for player p2: state3"));
    }

}