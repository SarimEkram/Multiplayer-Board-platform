package MatchmakingLeaderboard;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static MatchmakingLeaderboard.GameType.*;

import java.util.List;

public class PlayerDatabaseTest {
    private static final String TEST_USERNAME = "testplayer01";
    private static final int TEST_LEVEL = 5;
    private static final int TEST_USER_ID = 100001;

    @Test
    void testSaveAndRetrievePlayer() {
        Player player = new Player(TEST_USERNAME, TEST_LEVEL, TEST_USER_ID);
        boolean saved = PlayerDatabase.savePlayer(player);
        assertTrue(saved);

        Player retrieved = PlayerDatabase.getPlayerByUserID(TEST_USER_ID);
        assertNotNull(retrieved);
        assertEquals(TEST_USERNAME, retrieved.getUsername());

        // Clean up
        PlayerDatabase.deletePlayer(TEST_USER_ID);
    }

    @Test
    void testUpdatePlayer() {
        int testId = 100002;
        Player original = new Player("original", 1, testId);
        PlayerDatabase.savePlayer(original);

        Player updated = new Player("updated", 2, testId);
        boolean result = PlayerDatabase.savePlayer(updated);

        assertTrue(result);
        assertEquals("updated", PlayerDatabase.getPlayerByUserID(testId).getUsername());

        // Clean up
        PlayerDatabase.deletePlayer(testId);
    }

    @Test
    void testDeletePlayer() {
        int testId = 100003;
        Player player = new Player("todelete", 1, testId);
        PlayerDatabase.savePlayer(player);

        boolean deleted = PlayerDatabase.deletePlayer(testId);
        assertTrue(deleted);
        assertNull(PlayerDatabase.getPlayerByUserID(testId));
    }

    @Test
    void testGetPlayerByUsername() {
        String testUsername = "testuser_" + System.currentTimeMillis();
        int testId = 100004;
        Player player = new Player(testUsername, 1, testId);
        PlayerDatabase.savePlayer(player);

        Player found = PlayerDatabase.getPlayerByUsername(testUsername);
        assertNotNull(found);
        assertEquals(testId, found.getUserID());

        // Clean up
        PlayerDatabase.deletePlayer(testId);
    }

    @Test
    void testGetAllPlayers() {
        int testId1 = 100005;
        int testId2 = 100006;
        Player player1 = new Player("player1", 1, testId1);
        Player player2 = new Player("player2", 2, testId2);

        PlayerDatabase.savePlayer(player1);
        PlayerDatabase.savePlayer(player2);

        List<Player> allPlayers = PlayerDatabase.getAllPlayers();
        assertTrue(allPlayers.size() >= 2); // Others may exist

        // Clean up
        PlayerDatabase.deletePlayer(testId1);
        PlayerDatabase.deletePlayer(testId2);
    }

    @Test
    void testGameSpecificDataHandling() {
        int testId = 100007;
        Player player = new Player("gametest", 1, testId);
        player.addWin(TIC_TAC_TOE);
        PlayerDatabase.savePlayer(player);

        Player retrieved = PlayerDatabase.getPlayerByUserID(testId);
        assertEquals(1, retrieved.getWins(TIC_TAC_TOE));

        // Clean up
        PlayerDatabase.deletePlayer(testId);
    }

    @Test
    void testDuplicatePlayerHandling() {
        int testId = 100008;
        Player player1 = new Player("duplicate", 1, testId);
        Player player2 = new Player("duplicate", 2, testId);

        PlayerDatabase.savePlayer(player1);
        PlayerDatabase.savePlayer(player2);

        Player retrieved = PlayerDatabase.getPlayerByUserID(testId);
        assertEquals(2, retrieved.getLevel());

        // Clean up
        PlayerDatabase.deletePlayer(testId);
    }

    @Test
    void testNonexistentPlayerRetrieval() {
        int nonExistentId = 999999;
        String nonExistentUsername = "nonexistent_" + System.currentTimeMillis();

        assertNull(PlayerDatabase.getPlayerByUserID(nonExistentId));
        assertNull(PlayerDatabase.getPlayerByUsername(nonExistentUsername));
    }
}