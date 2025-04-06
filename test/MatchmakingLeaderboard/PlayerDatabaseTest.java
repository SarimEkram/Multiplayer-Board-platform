package MatchmakingLeaderboard;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static MatchmakingLeaderboard.GameType.*;

public class PlayerDatabaseTest {
    private Player testPlayer;

    /**
     * Initializes test player with sample data
     */
    @BeforeEach
    void setUp() {
        testPlayer = new Player("Ramesh", 5, 123456);
        testPlayer.addWin(TIC_TAC_TOE);
        testPlayer.addLoss(CONNECT_FOUR);
        testPlayer.setMMR(1500, CHECKERS);
        testPlayer.setGameSignal(1, TIC_TAC_TOE);
        testPlayer.setRank(new Rank(100), TIC_TAC_TOE);
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
        assertNotNull(loadedPlayer, "Player should be loaded from database");

        // Verify loaded data
        assertEquals("Ramesh", loadedPlayer.getUsername());
        assertEquals(5, loadedPlayer.getLevel());
        assertEquals(1, loadedPlayer.getWins(TIC_TAC_TOE));
        assertEquals(1, loadedPlayer.getLosses(CONNECT_FOUR));
        assertEquals(1500, loadedPlayer.getMMR(CHECKERS));
        assertEquals(1, loadedPlayer.getGameSignal(TIC_TAC_TOE));
        assertEquals(100, loadedPlayer.getRank(TIC_TAC_TOE).getRankingPoints());
    }

    /**
     * Test player data after updating its basic data
     */
    @Test
    void testUpdatePlayer() {
        // Initial save
        PlayerDatabase.savePlayer(testPlayer);

        // Update player data
        testPlayer.setLevel(10);
        testPlayer.addWin(TIC_TAC_TOE);
        testPlayer.setMMR(1600, CHECKERS);
        PlayerDatabase.savePlayer(testPlayer);

        // Reload and verify updates
        PlayerDatabase.loadPlayersFromCSV();
        Player updatedPlayer = PlayerDatabase.getPlayerByUserID(123456);

        assertEquals(10, updatedPlayer.getLevel());
        assertEquals(2, updatedPlayer.getWins(TIC_TAC_TOE));
        assertEquals(1600, updatedPlayer.getMMR(CHECKERS));
    }

    /**
     * Test player deletion
     */
    @Test
    void testDeletePlayer() {
        // Save first
        PlayerDatabase.savePlayer(testPlayer);
        assertNotNull(PlayerDatabase.getPlayerByUserID(123456), "Player should exist before deletion");

        // Then delete
        assertTrue(PlayerDatabase.deletePlayer(123456));
        assertNull(PlayerDatabase.getPlayerByUserID(123456), "Player should be deleted");
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
        player2.addWin(CONNECT_FOUR);
        player2.setMMR(1200, CONNECT_FOUR);

        PlayerDatabase.savePlayer(testPlayer);
        PlayerDatabase.savePlayer(player2);

        Player loaded1 = PlayerDatabase.getPlayerByUserID(123456);
        Player loaded2 = PlayerDatabase.getPlayerByUserID(654321);

        assertNotNull(loaded1, "First player should exist");
        assertNotNull(loaded2, "Second player should exist");
        assertEquals("Ramesh", loaded1.getUsername());
        assertEquals("Suresh", loaded2.getUsername());
        assertEquals(1200, loaded2.getMMR(CONNECT_FOUR));
    }

    /**
     * Tests getting player by username
     */
    @Test
    void testGetPlayerByUsername() {
        PlayerDatabase.savePlayer(testPlayer);
        Player foundPlayer = PlayerDatabase.getPlayerByUsername("Ramesh");
        assertNotNull(foundPlayer, "Player should be found by username");
        assertEquals(123456, foundPlayer.getUserID());
    }
}