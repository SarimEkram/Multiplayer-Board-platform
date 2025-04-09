package MatchmakingLeaderboard;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static MatchmakingLeaderboard.GameType.*;

import java.util.List;

public class PlayerDatabaseTest {
    private static final String username = "testplayer01";
    private static final int level = 5;
    private static final int userID = 100001;

    @Test
    void testSaveAndRetrievePlayer() {
        Player player = new Player(username, level, userID);
        boolean saved = PlayerDatabase.savePlayer(player);
        assertTrue(saved);

        Player retrieved = PlayerDatabase.getPlayerByUserID(userID);
        assertNotNull(retrieved);
        assertEquals(username, retrieved.getUsername());

        // Clean up
        PlayerDatabase.deletePlayer(userID);
    }

    @Test
    void testUpdatePlayer() {

        Player original = new Player("p1", 1, 100002);
        PlayerDatabase.savePlayer(original);

        Player updated = new Player("P1", 2, 100002);
        boolean result = PlayerDatabase.savePlayer(updated);

        assertTrue(result);
        assertEquals("P1", PlayerDatabase.getPlayerByUserID(100002).getUsername());

        // Clean up
        PlayerDatabase.deletePlayer(100002);
    }

    @Test
    void testDeletePlayer() {

        Player player = new Player("p1", 1, 100003);
        PlayerDatabase.savePlayer(player);

        boolean deleted = PlayerDatabase.deletePlayer(100003);
        assertTrue(deleted);
        assertNull(PlayerDatabase.getPlayerByUserID(100003));
    }

    @Test
    void testGetPlayerByUsername() {
        String testUsername = "testuser_" + System.currentTimeMillis();

        Player player = new Player(testUsername, 1, 100004);
        PlayerDatabase.savePlayer(player);

        Player found = PlayerDatabase.getPlayerByUsername(testUsername);
        assertNotNull(found);
        assertEquals(100004, found.getUserID());

        // Clean up
        PlayerDatabase.deletePlayer(100004);
    }

    @Test
    void testGetAllPlayers() {

        Player player1 = new Player("player1", 1, 100005);
        Player player2 = new Player("player2", 2, 100006);

        PlayerDatabase.savePlayer(player1);
        PlayerDatabase.savePlayer(player2);

        List<Player> allPlayers = PlayerDatabase.getAllPlayers();
        assertTrue(allPlayers.size() >= 2); // Others may exist

        // Clean up
        PlayerDatabase.deletePlayer(100005);
        PlayerDatabase.deletePlayer(100006);
    }

    @Test
    void testGameSpecificDataHandling() {

        Player player = new Player("gametest", 1, 100007);
        player.addWin(TIC_TAC_TOE);
        PlayerDatabase.savePlayer(player);

        Player retrieved = PlayerDatabase.getPlayerByUserID(100007);
        assertEquals(1, retrieved.getWins(TIC_TAC_TOE));

        // Clean up
        PlayerDatabase.deletePlayer(100007);
    }

    @Test
    void testDuplicatePlayerHandling() {

        Player player1 = new Player("duplicate", 1, 100008);
        Player player2 = new Player("duplicate", 2, 100008);

        PlayerDatabase.savePlayer(player1);
        PlayerDatabase.savePlayer(player2);

        Player retrieved = PlayerDatabase.getPlayerByUserID(100008);
        assertEquals(2, retrieved.getLevel());

        // Clean up
        PlayerDatabase.deletePlayer(100008);
    }

    @Test
    void testNonexistentPlayerRetrieval() {
        int nonExistentId = 999999;
        String nonExistentUsername = "nonexistent_" + System.currentTimeMillis();

        assertNull(PlayerDatabase.getPlayerByUserID(nonExistentId));
        assertNull(PlayerDatabase.getPlayerByUsername(nonExistentUsername));
    }
}