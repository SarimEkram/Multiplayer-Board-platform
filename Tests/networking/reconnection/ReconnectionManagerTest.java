package networking.reconnection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import java.util.HashSet;
import java.util.Set;

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
        Assertions.assertTrue(output.contains("Game started with players:"));
        Assertions.assertTrue(output.contains("p1"));
        Assertions.assertTrue(output.contains("p2"));
        Assertions.assertTrue(output.contains("p3"));
    }

    @Test
    public void testStartGameWithEmptyPlayers() {
        ReconnectionManager manager = new ReconnectionManager("game2");
        Set<String> players = new HashSet<>();
        manager.startGame(players);
        String output = outContent.toString().trim();
        // Depending on HashSet's toString, it should display an empty collection.
        Assertions.assertTrue(output.contains("Game started with players:"));
        Assertions.assertTrue(output.contains("[]"));
    }

    // --- Tests for saveGameState ---
    @Test
    public void testSaveGameState_NewState() {
        ReconnectionManager manager = new ReconnectionManager("game3");
        TestGameState state1 = new TestGameState("state1");
        manager.saveGameState("p1", state1);
        String output = outContent.toString().trim();
        Assertions.assertTrue(output.contains("Saved game state for player p1: state1"));
    }

//TODO: Fix error in the below test

//    @Test
//    public void testSaveGameState_UpdateState() {
//        ReconnectionManager manager = new ReconnectionManager("game4");
//        TestGameState state1 = new TestGameState("state1");
//        GameState state2 = new GameState("state2");
//        manager.saveGameState("p1", state1);
//        outContent.reset();
//        manager.saveGameState("p1", state2);
//        String output = outContent.toString().trim();
//        Assertions.assertTrue(output.contains("Saved game state for player p1: state2"));
//    }
    @Test
    public void testSaveGameState_NewPlayer() {
        ReconnectionManager manager = new ReconnectionManager("game5");
        TestGameState state3 = new TestGameState("state3");
        manager.saveGameState("p2", state3);
        String output = outContent.toString().trim();
        Assertions.assertTrue(output.contains("Saved game state for player p2: state3"));
    }
    // --- Tests for playerDisconnected ---

    @Test
    public void testPlayerDisconnected_WhenConnected() {
        ReconnectionManager manager = new ReconnectionManager("game6");
        Set<String> players = new HashSet<>(Arrays.asList("p1"));
        manager.startGame(players);
        outContent.reset();
        manager.playerDisconnected("p1");
        String output = outContent.toString().trim();
        Assertions.assertTrue(output.contains("Player p1 disconnected."));
    }

    @Test
    public void testPlayerDisconnected_WhenNotConnected() {
        ReconnectionManager manager = new ReconnectionManager("game7");
        manager.playerDisconnected("p2");
        String output = outContent.toString().trim();
        Assertions.assertTrue(output.contains("Player p2 was not connected."));
    }

    // --- Tests for attemptReconnection ---

    @Test
    public void testAttemptReconnection_Success() {
        ReconnectionManager manager = new ReconnectionManager("game8");
        // Setup: start game and disconnect a player with saved state.
        Set<String> players = new HashSet<>(Arrays.asList("p1", "p2"));
        manager.startGame(players);
        manager.playerDisconnected("p1");
        TestGameState state1 = new TestGameState("state1");
        manager.saveGameState("p1", state1);
        outContent.reset();
        boolean result = manager.attemptReconnection("p1");
        String output = outContent.toString().trim();
        Assertions.assertTrue(result);
        Assertions.assertTrue(output.contains("Reconnecting player p1..."));
        Assertions.assertTrue(output.contains("Restoring game state for player p1: state1"));
        Assertions.assertTrue(output.contains("Notifying player p2 that p1 has reconnected."));
    }

    @Test
    public void testAttemptReconnection_NotMarkedAsDisconnected() {
        ReconnectionManager manager = new ReconnectionManager("game9");
        Set<String> players = new HashSet<>(Arrays.asList("p1"));
        manager.startGame(players);
        outContent.reset();
        boolean result = manager.attemptReconnection("p1");
        String output = outContent.toString().trim();
        Assertions.assertFalse(result);
        Assertions.assertTrue(output.contains("Player p1 is not marked as disconnected."));
    }

    @Test
    public void testAttemptReconnection_SessionInactive() {
        ReconnectionManager manager = new ReconnectionManager("game10");
        Set<String> players = new HashSet<>(Arrays.asList("p1"));
        manager.startGame(players);
        manager.playerDisconnected("p1");
        manager.endGame();
        outContent.reset();
        boolean result = manager.attemptReconnection("p1");
        String output = outContent.toString().trim();
        Assertions.assertFalse(result);
        Assertions.assertTrue(output.contains("Game session is not active. Cannot reconnect player p1."));
    }

}