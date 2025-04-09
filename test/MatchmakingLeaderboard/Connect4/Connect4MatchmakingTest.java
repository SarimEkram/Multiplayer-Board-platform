package MatchmakingLeaderboard.Connect4;

import MatchmakingLeaderboard.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains the unit tests for the Connect4Matchmaking class.
 * It verifies queue management, matchmaking checks, player compatibility, and game signal functionalities.
 */
public class Connect4MatchmakingTest {
    private Connect4Matchmaking matchmaking;
    private Player player1, player2;
    private int gameType = 1;

    /**
     * Initializes a new matchmaking instance and two player objects before each test.
     */
    @BeforeEach
    public void setup() {
        matchmaking = new Connect4Matchmaking();
        player1 = new Player("Delta", 10, 987321);
        player2 = new Player("Alice", 15, 123789);
    }

    /**
     * Simulates repeated connection attempts and checks if NetworkFailureException is thrown.
     * Also ignores general IOExceptions during this process.
     */
    @Test()
    public void testConstructor_NetworkFailureException() {
        for (int i = 0; i < 100; i++) {
            try {
                matchmaking.matchmakingConnect(); // Attempt to connect
            } catch (NetworkFailureException e) {
                System.out.println("A Network Failure Exception Detected"); // Expected outcome
                break;
            } catch (IOException ignored) {
                // Ignore other IOExceptions
            }
        }
    }

    /**
     * Tests adding a player to the matchmaking queue.
     */
    @Test
    public void testJoinQueue() {
        matchmaking.joinQueue(player1); // Add to queue
    }

    /**
     * Tests removing a player from the queue after they have joined.
     */
    @Test
    public void testLeaveQueue() {
        matchmaking.joinQueue(player1); // Join first
        matchmaking.leaveQueue(player1); // Then leave
    }

    /**
     * Tests removing a player from the queue when the queue is empty.
     */
    @Test
    public void testLeaveQueue_EmptyQueue() {
        matchmaking.leaveQueue(player1); // Remove without joining
    }

    /**
     * Verifies the matchmaking system reports as active (up).
     */
    @Test
    public void testCheckMatchmaking_Up() {
        matchmaking = new Connect4Matchmaking();
        matchmaking.matchmakingUp = true; // Force matchmaking up
        assertTrue(matchmaking.checkMatchmaking()); // Should return true
    }

    /**
     * Verifies the matchmaking system reports as inactive (down).
     */
    @Test
    public void testCheckMatchmaking_Down() {
        matchmaking = new Connect4Matchmaking();
        matchmaking.matchmakingUp = false; // Force matchmaking down
        assertFalse(matchmaking.checkMatchmaking()); // Should return false
    }

    /**
     * Tests player compatibility when all criteria are satisfied (rank, level, game signal).
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
     * Tests player incompatibility due to rank difference.
     */
    @Test
    public void testCheckPlayers_NotCompatibleOne() {
        player1.setRank(new Rank(RankTier.BRONZE), GameType.CONNECT_FOUR);
        player2.setRank(new Rank(RankTier.BRONZE), GameType.CONNECT_FOUR);
        player1.setGameSignal(2, GameType.CONNECT_FOUR);
        player2.setGameSignal(2, GameType.CONNECT_FOUR);
        player1.setLevel(38);
        player2.setLevel(26);

        assertFalse(matchmaking.checkPlayers(player1, player2)); // Rank too different
    }

    /**
     * Tests player incompatibility due to a large skill gap.
     */
    @Test
    public void testCheckPlayers_NotCompatibleTwo() {

        player1.getRank(GameType.CONNECT_FOUR).adjustPoints(player1,2500,GameType.CONNECT_FOUR);
        player1.getRank(GameType.CONNECT_FOUR).adjustPoints(player1,500,GameType.CONNECT_FOUR);
        player1.setGameSignal(2, GameType.CONNECT_FOUR);
        player2.setGameSignal(2, GameType.CONNECT_FOUR);
        player1.setLevel(27);
        player2.setLevel(26);

        assertFalse(matchmaking.checkPlayers(player1, player2)); // Skill gap too large
    }

    /**
     * Tests player incompatibility when one or both have not set game signals.
     */
    @Test
    public void testCheckPlayers_NotCompatibleThree() {
        player1.setRank(new Rank(RankTier.GOLD), GameType.CONNECT_FOUR);
        player2.setRank(new Rank(RankTier.BRONZE), GameType.CONNECT_FOUR);
        player1.setGameSignal(1, GameType.TIC_TAC_TOE); // Different game
        player1.setGameSignal(0,GameType.CONNECT_FOUR);
        player2.setGameSignal(1, GameType.CONNECT_FOUR);
        player1.setLevel(27);
        player2.setLevel(26);

        assertFalse(matchmaking.checkPlayers(player1, player2)); // Missing game signal
    }

    /**
     * Tests the matchmaking system's ability to find opponents for specific players
     */
    @Test
    public void testFindOpponent(){
        player1.setRank(new Rank(RankTier.BRONZE), GameType.CONNECT_FOUR);
        player1.setGameSignal(2, GameType.CONNECT_FOUR);
        player1.setLevel(17);

        player2.setRank(new Rank(RankTier.BRONZE), GameType.CONNECT_FOUR);
        player2.setGameSignal(2,GameType.CONNECT_FOUR);
        player2.setLevel(18);
        PlayerDatabase.savePlayer(player1);
        PlayerDatabase.savePlayer(player2);
        matchmaking.joinQueue(player2);
        Player opponent = matchmaking.findOpponent(player1.getUserID());
        assertEquals(player2.getUserID(), opponent.getUserID());
    }

    /**
     * Tests initiating a match between two players.
     */
    @Test
    public void testFindMatch() {
        matchmaking.findMatch(player1, player2); // Attempt match
    }

    /**
     * Tests signaling the start of a game.
     */
    @Test
    public void testSignalStartGame() {
        matchmaking.signalStartGame(); // Trigger start signal
    }

    /**
     * Tests signaling the addition of a player to a game.
     */
    @Test
    public void testSignalAddPlayer() {
        matchmaking.signalAddPlayer(player1); // Simulate signal
    }

    /**
     * Tests starting the matchmaking process with an empty queue.
     */
    @Test
    public void MatchmakingWithoutPlayers() {
        matchmaking.startMatchmaking(); // No players joined
    }

    /**
     * Tests matchmaking behavior when only one player is present in the queue.
     */
    @Test
    public void MatchmakingWithOnePlayer() {
        matchmaking.joinQueue(player1);
        matchmaking.startMatchmaking(); // Only one player queued
    }
}
