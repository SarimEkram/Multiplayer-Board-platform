package networking.game;

import networking.game.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.lang.reflect.*;


public class GameScoreSenderTest {
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testEstablishConnection() {
        GameScoreSender sender = new GameScoreSender("game123");
        sender.establishConnection();
        String output = outContent.toString().trim();
        assertTrue(output.contains("Connected to Server for sending scores."), "Expected connection message");
    }

    @Test
    public void testCloseConnection() {
        GameScoreSender sender = new GameScoreSender("game123");
        sender.establishConnection();
        outContent.reset();
        sender.closeConnection();
        String output = outContent.toString().trim();
        assertTrue(output.contains("Disconnected from Server for sending scores."), "Expected disconnection message");
    }

    @Test
    public void testSendScoresWhenNotConnected() {
        GameScoreSender sender = new GameScoreSender("game123");
        sender.sendScores("player1", 1);
        String output = outContent.toString().trim();
        assertTrue(output.contains("Error: Cannot send scores. Server is not connected."), "Expected error for not connected");
    }

    @Test
    public void testSendScoresWhenGameNotOver() {
        GameScoreSender sender = new GameScoreSender("game123");
        sender.establishConnection();
        outContent.reset();
        sender.sendScores("player1", 1);
        String output = outContent.toString().trim();
        assertTrue(output.contains("Error: Cannot send scores. Game is not over."), "Expected error for game not over");
    }

    @Test
    public void testSendValidScore() {
        GameScoreSender sender = new GameScoreSender("game123");
        sender.establishConnection();
        sender.markGameOver();

        sender.sendScores("player1", 1);

        String output = outContent.toString().trim();
        assertTrue(output.contains("scores sent for player1: 1 for game: game123"), "Should print success message");

    }

    @Test
    public void testSendInvalidScore() {
        GameScoreSender sender = new GameScoreSender("game123");
        sender.establishConnection();
        sender.markGameOver();

        sender.sendScores("player1", 5);

        String output = outContent.toString().trim();
        assertTrue(output.contains("Enter valid score") || output.contains("Error: Score must be between 0 and 1"),
                "Should print error message for invalid score");
    }
    @Test
    public void testDuplicateScoreForSamePlayer() {
        GameScoreSender sender = new GameScoreSender("game123");
        sender.establishConnection();
        sender.markGameOver();
        sender.sendScores("player1", 1);
        outContent.reset();
        sender.sendScores("player1", 1);
        String output = outContent.toString().trim();

        assertTrue(output.contains("Score already exists for player player1") ||
                        output.contains("Enter valid score"),
                "Should print error for duplicate score");

        assertFalse(output.contains("Scores sent to player: player1 for game game123"),
                "Duplicate score should not be accepted");
    }

    @Test
    public void testSendGameUpdateWhenNotConnected() {
        GameScoreSender sender = new GameScoreSender("game123");
        sender.sendGameUpdate("Update 1");
        String output = outContent.toString().trim();
        assertTrue(output.contains("Error: Cannot send game update. Server not connected."), "Expected error message for game update without connection");
    }

    @Test
    public void testSendGameUpdateWhenConnected() {
        GameScoreSender sender = new GameScoreSender("game123");
        sender.establishConnection();
        outContent.reset();
        sender.sendGameUpdate("Update 1");
        String output = outContent.toString().trim();
        assertTrue(output.contains("Sending game update: Update 1"), "Expected sending game update message");
    }

    @Test
    public void testReceiveGameUpdateWhenNotConnected() {
        GameScoreSender sender = new GameScoreSender("game123");
        sender.receiveGameUpdate();
        String output = outContent.toString().trim();
        assertTrue(output.contains("Error: Cannot receive game update. Server not connected."), "Expected error message for receiving update without connection");
    }




}
