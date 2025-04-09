package MatchmakingLeaderboard.Connect4;

import MatchmakingLeaderboard.Connect4.Leaderboard.Connect4Leaderboard;
import MatchmakingLeaderboard.GameType;
import MatchmakingLeaderboard.Rank;
import MatchmakingLeaderboard.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Connect4LeaderboardTest {

    private Connect4Leaderboard leaderboard;
    private Player player1;
    private Player player2;
    private Player player3;

    private final GameType gameType = GameType.CONNECT_FOUR;

    /**
     * Initializes the leaderboard with three sample players before each test.
     */
    @BeforeEach
    void setUp() {
        leaderboard = Connect4Leaderboard.getInstance();

        player1 = new Player("Tom", 10, 101010);
        player2 = new Player("Bob", 8, 202020);
        player3 = new Player("Jerry", 12, 303030);

        player1.setMMR(1500, gameType);
        player2.setMMR(1200, gameType);
        player3.setMMR(1800, gameType);

        player1.setRank(new Rank(), gameType);
        player2.setRank(new Rank(), gameType);
        player3.setRank(new Rank(), gameType);

        leaderboard.addPlayer(player1, gameType);
        leaderboard.addPlayer(player2, gameType);
        leaderboard.addPlayer(player3, gameType);
    }

    /**
     * Verifies that a new player is added to the leaderboard.
     */
    @Test
    void testAddPlayerToLeaderboard() {
        Player newPlayer = new Player("Dave", 5, 404040);
        newPlayer.setMMR(1000, gameType);
        newPlayer.setRank(new Rank(), gameType);

        leaderboard.addPlayer(newPlayer, gameType);

        List<Player> players = leaderboard.getScores();
        assertTrue(players.contains(newPlayer));
    }

    /**
     * Ensures NullPointerException is thrown when trying to add a null player.
     */
    @Test
    void testAddNullPlayerThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> leaderboard.addPlayer(null, gameType));
    }

    /**
     * Ensures a newly added high-MMR player is sorted to the top of the leaderboard.
     */
    @Test
    void testNewPlayerIsSortedInLeaderboard() {
        Player newPlayer = new Player("Eve", 7, 505050);
        newPlayer.setMMR(2000, gameType); // highest MMR
        newPlayer.setRank(new Rank(), gameType);

        Connect4Leaderboard.updatePlayer(newPlayer, true, gameType);

        List<Player> scores = leaderboard.getScores();
        assertEquals(newPlayer, scores.get(0)); // should be at top
    }

    /**
     * Confirms players are sorted in descending order by MMR.
     */
    @Test
    void testSortsPlayerMmrScores() {
        List<Player> scores = leaderboard.getScores();
        for (int i = 0; i < scores.size() - 1; i++) {
            assertTrue(scores.get(i).getMMR(gameType) >= scores.get(i + 1).getMMR(gameType));
        }
    }

    /**
     * Tests finding a player by valid user ID.
     */
    @Test
    void testFindExistingPlayer() {
        Player found = leaderboard.findPLayerById(202020);
        assertNotNull(found);
        assertEquals("Bob", found.getUsername());
    }

    /**
     * Tests that finding a player with an unknown ID returns null.
     */
    @Test
    void testFindPlayerByNonExistingIds() {
        Player found = leaderboard.findPLayerById(999999);
        assertNull(found);
    }

    /**
     * Ensures that the leaderboard instance follows the singleton pattern.
     */
    @Test
    void testLeaderboardIsSingletonInstance() {
        Connect4Leaderboard anotherInstance = Connect4Leaderboard.getInstance();
        assertSame(leaderboard, anotherInstance);
    }
}
