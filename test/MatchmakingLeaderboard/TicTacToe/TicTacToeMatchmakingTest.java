package MatchmakingLeaderboard.TicTacToe;

import MatchmakingLeaderboard.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the TicTacToeMatchmaking system.
 * This class tests various functionalities including queue management,
 * matchmaking status, player compatibility checks, and signal handling.
 */
public class TicTacToeMatchmakingTest {
    private TicTacToeMatchmaking matchmaking;
    private Player player1, player2;
    /**
     * Initializes a new matchmaking instance and two players before each test.
     */
    @BeforeEach
    public void setup() {
        matchmaking = new TicTacToeMatchmaking();
        player1 = new Player("Bob", 10, 123456);
        player2 = new Player("Charlie", 15, 987654);
    }

    /**
     * Tests that a NetworkFailureException can be thrown during connection attempts.
     * It tries to connect multiple times to simulate network issues.
     */
    @Test()
    public void testConstructor_NetworkFailureException() {
        for (int i = 0; i < 100; i++) {
            try {
                matchmaking.matchmakingConnect(); // Attempt connection
            } catch (NetworkFailureException e) {
                System.out.println("A Network Failure Exception Detected"); // Expected exception
                break;
            } catch (IOException ignored) {
                // Ignore other IOExceptions
            }
        }
    }

    /**
     * Tests that a player can be added to the matchmaking queue.
     */
    @Test
    public void testJoinQueue() {
        matchmaking.joinQueue(player1); // Add player to queue
    }

    /**
     * Tests that a player can leave the matchmaking queue after joining.
     */
    @Test
    public void testLeaveQueue() {
        matchmaking.joinQueue(player1); // Join first
        matchmaking.leaveQueue(player1); // Then leave
    }

    /**
     * Tests that calling leaveQueue on a player not in the queue does not cause errors.
     */
    @Test
    public void testLeaveQueue_EmptyQueue() {
        matchmaking.leaveQueue(player1); // Leave without joining
    }

    /**
     * Tests the matchmaking system when it is operational.
     */
    @Test
    public void testCheckMatchmaking_Up() {
        assertTrue(matchmaking.checkMatchmaking()); // Should be true by default
    }

    /**
     * Tests the matchmaking system when it is manually set to be down.
     */
    @Test
    public void testCheckMatchmaking_Down() {
        matchmaking = new TicTacToeMatchmaking();
        matchmaking.matchmakingUp = false; // Force matchmaking to down state
        assertFalse(matchmaking.checkMatchmaking()); // Should return false
    }

    /**
     * Tests compatibility of two players who meet matchmaking criteria.
     */
    @Test
    public void testCheckPlayers_Compatible() {
        player1.setRank(new Rank(RankTier.BRONZE), GameType.TIC_TAC_TOE);
        player2.setRank(new Rank(RankTier.BRONZE), GameType.TIC_TAC_TOE);
        player1.setGameSignal(1, GameType.TIC_TAC_TOE);
        player2.setGameSignal(1, GameType.TIC_TAC_TOE);
        player1.setLevel(20);
        player2.setLevel(26);

        assertTrue(matchmaking.checkPlayers(player1, player2)); // Should be compatible
    }

    /**
     * Tests compatibility of players with different ranks.
     */
    @Test
    public void testCheckPlayers_NotCompatibleOne() {
        player1.setRank(new Rank(RankTier.BRONZE), GameType.TIC_TAC_TOE);
        player2.setRank(new Rank(RankTier.BRONZE), GameType.TIC_TAC_TOE);
        player1.setGameSignal(1, GameType.TIC_TAC_TOE);
        player2.setGameSignal(1, GameType.TIC_TAC_TOE);
        player1.setLevel(38);
        player2.setLevel(26);
        assertFalse(matchmaking.checkPlayers(player1, player2)); // Not compatible due to rank difference
    }

    /**
     * Tests compatibility of players with significantly different ranks.
     */
    @Test
    public void testCheckPlayers_NotCompatibleTwo() {
        player1.setRank(new Rank(RankTier.GOLD), GameType.TIC_TAC_TOE);
        player2.setRank(new Rank(RankTier.BRONZE), GameType.TIC_TAC_TOE);
        player1.setGameSignal(1, GameType.TIC_TAC_TOE);
        player2.setGameSignal(1, GameType.TIC_TAC_TOE);
        player1.setLevel(27);
        player2.setLevel(26);
        assertFalse(matchmaking.checkPlayers(player1, player2)); // Rank too far apart
    }

    /**
     * Tests compatibility of players trying to play different games.
     */
    @Test
    public void testCheckPlayers_NotCompatibleThree() {
        player1.setRank(new Rank(RankTier.GOLD), GameType.TIC_TAC_TOE);
        player2.setRank(new Rank(RankTier.BRONZE), GameType.TIC_TAC_TOE);
        player1.setGameSignal(2, GameType.CONNECT_FOUR); // Different game
        player2.setGameSignal(1, GameType.TIC_TAC_TOE);
        player1.setLevel(27);
        player2.setLevel(26);
        assertFalse(matchmaking.checkPlayers(player1, player2)); // Game mismatch
    }

    /**
     * Tests the matchmaking system's ability to find opponents for specific players
     */
    @Test
    public void testFindOpponent(){
        player1.setRank(new Rank(RankTier.BRONZE), GameType.TIC_TAC_TOE);
        player1.setGameSignal(1, GameType.TIC_TAC_TOE);
        player1.setLevel(17);

        player2.setRank(new Rank(RankTier.BRONZE), GameType.TIC_TAC_TOE);
        player2.setGameSignal(1,GameType.TIC_TAC_TOE);
        player2.setLevel(18);
        PlayerDatabase.savePlayer(player1);
        PlayerDatabase.savePlayer(player2);
        matchmaking.joinQueue(player2);
        Player opponent = matchmaking.findOpponent(player1.getUserID());
        assertEquals(player2.getUserID(), opponent.getUserID());
    }

    /**
     * Tests the matchmaking system's ability to initiate a match between two players.
     */
    @Test
    public void testFindMatch() {
        matchmaking.findMatch(player1, player2); // Simulate match finding
    }

    /**
     * Tests if the signal to start a game works correctly.
     */
    @Test
    public void testSignalStartGame() {
        matchmaking.signalStartGame(); // Trigger game start
    }

    /**
     * Tests if the system handles signaling the addition of a player.
     */
    @Test
    public void testSignalAddPlayer() {
        matchmaking.signalAddPlayer(player1); // Simulate player signal
    }

    /**
     * Tests starting matchmaking with no players in the queue.
     */
    @Test
    public void MatchmakingWithoutPlayers() {
        matchmaking.startMatchmaking(); // Should handle empty case
    }

    /**
     * Tests starting matchmaking with only one player in the queue.
     */
    @Test
    public void MatchmakingWithOnePlayer() {
        matchmaking.joinQueue(player1);
        matchmaking.startMatchmaking(); // Only one player queued
    }
}
