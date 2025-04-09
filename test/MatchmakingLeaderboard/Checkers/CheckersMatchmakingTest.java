package MatchmakingLeaderboard.Checkers;

import MatchmakingLeaderboard.*;
import MatchmakingLeaderboard.Checkers.Matchmaking.CheckersMatchmaking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CheckersMatchmaking class.
 * This test suite validates matchmaking setup, queue management,
 * player compatibility checks, and matchmaking flow control for the Checkers game type.
 */
public class CheckersMatchmakingTest {
    private CheckersMatchmaking matchmaking;
    private Player player1, player2;

    /**
     * Initializes a new instance of CheckersMatchmaking and two test players before each test.
     */
    @BeforeEach
    public void setup() {
        matchmaking = new CheckersMatchmaking();
        player1 = new Player("Echo", 10, 567567);
        player2 = new Player("Ferrera", 15, 765765);
    }

    /**
     * Tests whether the matchmaking connection can handle network failures gracefully.
     */
    @Test()
    public void testConstructor_NetworkFailureException() {
        for (int i = 0; i < 100; i++) {
            try {
                matchmaking.matchmakingConnect(); // Attempt connection
            } catch (NetworkFailureException e) {
                System.out.println("A Network Failure Exception Detected"); // Expected behavior
                break;
            } catch (IOException ignored) {
                // Ignore other IO exceptions
            }
        }
    }

    /**
     * Tests adding a player to the matchmaking queue.
     */
    @Test
    public void testJoinQueue() {
        matchmaking.joinQueue(player1); // Add player1 to the queue
    }

    /**
     * Tests removing a player from the queue after joining.
     */
    @Test
    public void testLeaveQueue() {
        matchmaking.joinQueue(player1); // Player joins
        matchmaking.leaveQueue(player1); // Then leaves
    }

    /**
     * Tests removing a player from the queue without the player having joined.
     */
    @Test
    public void testLeaveQueue_EmptyQueue() {
        matchmaking.leaveQueue(player1); // No prior join
    }

    /**
     * Tests that the matchmaking system correctly reports as active.
     */
    @Test
    public void testCheckMatchmaking_Up() {
        matchmaking = new CheckersMatchmaking();
        matchmaking.matchmakingUp = true; // Set matchmaking as up
        assertTrue(matchmaking.checkMatchmaking()); // Should return true
    }

    /**
     * Tests that the matchmaking system correctly reports as inactive.
     */
    @Test
    public void testCheckMatchmaking_Down() {
        matchmaking = new CheckersMatchmaking();
        matchmaking.matchmakingUp = false; // Set matchmaking as down
        assertFalse(matchmaking.checkMatchmaking()); // Should return false
    }

    /**
     * Verifies compatibility between two players based on level and rank.
     */
    @Test
    public void testCheckPlayers_Compatible() {
        player1.setRank(new Rank(RankTier.BRONZE), GameType.CHECKERS);
        player2.setRank(new Rank(RankTier.BRONZE), GameType.CHECKERS);
        player1.setLevel(20);
        player2.setLevel(26);

        assertTrue(matchmaking.checkPlayers(player1, player2)); // Should be compatible
    }

    /**
     * Tests incompatibility due to significant rank difference.
     */
    @Test
    public void testCheckPlayers_NotCompatibleOne() {
        player1.setRank(new Rank(RankTier.BRONZE), GameType.CHECKERS);
        player2.setRank(new Rank(RankTier.BRONZE), GameType.CHECKERS);
        player1.setLevel(38);
        player2.setLevel(26);

        assertFalse(matchmaking.checkPlayers(player1, player2)); // Too much rank gap
    }

    /**
     * Tests incompatibility due to extreme skill differences.
     */
    @Test
    public void testCheckPlayers_NotCompatibleTwo() {
        player1.setRank(new Rank(RankTier.GOLD), GameType.CHECKERS);
        player2.setRank(new Rank(RankTier.BRONZE), GameType.CHECKERS);
        player1.setLevel(27);
        player2.setLevel(26);

        assertFalse(matchmaking.checkPlayers(player1, player2)); // Skill gap too large
    }

    /**
     * Redundant test similar to testCheckPlayers_NotCompatibleTwo, for added validation.
     */
    @Test
    public void testCheckPlayers_NotCompatibleThree() {
        player1.setRank(new Rank(RankTier.GOLD), GameType.CHECKERS);
        player2.setRank(new Rank(RankTier.BRONZE), GameType.CHECKERS);
        player1.setLevel(27);
        player2.setLevel(26);

        assertFalse(matchmaking.checkPlayers(player1, player2)); // Same issue: large mismatch
    }

    /**
     * Tests the matchmaking system's ability to find opponents for specific players
     */
    @Test
    public void testFindOpponent(){
        player1.setRank(new Rank(RankTier.BRONZE), GameType.CHECKERS);
        player1.setGameSignal(3, GameType.CHECKERS);
        player1.setLevel(17);

        player2.setRank(new Rank(RankTier.BRONZE), GameType.CHECKERS);
        player2.setGameSignal(3,GameType.CHECKERS);
        player2.setLevel(18);
        PlayerDatabase.savePlayer(player1);
        PlayerDatabase.savePlayer(player2);
        matchmaking.joinQueue(player2);
        Player opponent = matchmaking.findOpponent(player1.getUserID());
        assertEquals(player2.getUserID(), opponent.getUserID());
    }

    /**
     * Tests initiating a match between two compatible players.
     */
    @Test
    public void testFindMatch() {
        matchmaking.findMatch(player1, player2); // Attempt to create match
    }

    /**
     * Tests signaling the start of a match.
     */
    @Test
    public void testSignalStartGame() {
        matchmaking.signalStartGame(); // Should trigger start logic
    }

    /**
     * Tests signaling a player joining the game.
     */
    @Test
    public void testSignalAddPlayer() {
        matchmaking.signalAddPlayer(player1); // Simulate adding a player to session
    }

    /**
     * Tests matchmaking behavior when the queue is empty.
     */
    @Test
    public void MatchmakingWithoutPlayers() {
        matchmaking.startMatchmaking(); // No players joined
    }

    /**
     * Tests matchmaking behavior with a single player in the queue.
     */
    @Test
    public void MatchmakingWithOnePlayer() {
        matchmaking.joinQueue(player1); // Join one player
        matchmaking.startMatchmaking(); // Start with only one
    }
}
