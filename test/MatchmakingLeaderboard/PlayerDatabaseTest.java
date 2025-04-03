package MatchmakingLeaderboard;


import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerDatabaseTest {
    Player testPlayer = new Player("Ramesh", 5, 123456);

    /**
     * Initializes test player with sample data
     */
    @BeforeEach
    void setUp() {
        testPlayer.addWin(1);
        testPlayer.addLoss(2);
        testPlayer.setMMR(1500, 3);
        testPlayer.setGameSignal(1, 1);
        testPlayer.setRank(new Rank(100), 1);
    }

    /**
     * Tests basic save and load functionality
     */
    @Test
    void testSaveAndLoadPlayer() {
        // Save the player
        assertTrue(PlayerDatabase.savePlayer(testPlayer));

        // Load players from file
        PlayerDatabase.loadPlayersFromCSV();

        // Retrieve the player
        Player loadedPlayer = PlayerDatabase.getPlayerByUserID(123456);
        assertNotNull(loadedPlayer, "Expected to be not null, but it wasn't");

        // Verify loaded data
        assertEquals("Ramesh", loadedPlayer.getUsername());
        assertEquals(5, loadedPlayer.getLevel());
        assertEquals(1, loadedPlayer.getWins(1));
        assertEquals(1, loadedPlayer.getLosses(2));
        assertEquals(1500, loadedPlayer.getMMR(3));
        assertEquals(1, loadedPlayer.getGameSignal(1));
        assertEquals(100, loadedPlayer.getRank(1).getRankingPoints());
    }

    /**
     * test player data after updating its basic data
     */
    @Test
    void testUpdatePlayer() {
        // Initial save
        PlayerDatabase.savePlayer(testPlayer);

        // Update player data
        testPlayer.setLevel(10);
        testPlayer.addWin(1);
        testPlayer.setMMR(1600, 3);
        PlayerDatabase.savePlayer(testPlayer);

        // Reload and verify updates
        PlayerDatabase.loadPlayersFromCSV();
        Player updatedPlayer = PlayerDatabase.getPlayerByUserID(123456);

        assertEquals(10, updatedPlayer.getLevel());
        assertEquals(2, updatedPlayer.getWins(1));
        assertEquals(1600, updatedPlayer.getMMR(3));
    }

    /**
     *  test player deletion
     */
    @Test
    void testDeletePlayer() {
        // Save first
        PlayerDatabase.savePlayer(testPlayer);
        assertNotNull(PlayerDatabase.getPlayerByUserID(123456), "Expected to be not null, but it wasn't");

        // Then delete
        assertTrue(PlayerDatabase.deletePlayer(123456));
        assertNull(PlayerDatabase.getPlayerByUserID(123456), "Expected to be null, but it wasn't");
    }

    /**
     * Tests handling of non-existent players
     */
    @Test
    void testNonexistentPlayer() {
        assertNull(PlayerDatabase.getPlayerByUserID(999999));
    }

    /**
     * Tests multiple player handling
     */
    @Test
    void testMultiplePlayers() {
        Player player2 = new Player("Suresh", 3, 654321);
        player2.addWin(2);
        player2.setMMR(1200, 2);

        PlayerDatabase.savePlayer(testPlayer);
        PlayerDatabase.savePlayer(player2);

        Player loaded1 = PlayerDatabase.getPlayerByUserID(123456);
        Player loaded2 = PlayerDatabase.getPlayerByUserID(654321);

        assertNotNull(loaded1, "Expected to be not null, but it wasn't");
        assertNotNull(loaded2, "Expected to be not null, but it wasn't");
        assertEquals("Ramesh", loaded1.getUsername());
        assertEquals("Suresh", loaded2.getUsername());
        assertEquals(1200, loaded2.getMMR(2));

    }
}
