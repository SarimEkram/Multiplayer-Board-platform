package MatchmakingLeaderboard;

import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static MatchmakingLeaderboard.GameType.*;

public class PlayerDatabaseTest {
    private Player testPlayer;
    private static final String testFile = "test_playerdata.csv";
    private static final String originalFile = "playerdata.csv";

    @BeforeAll
    static void setupAll() {
        // Backup original file if exists
        File original = new File(originalFile);
        if (original.exists()) {
            original.renameTo(new File(testFile));
        }
    }

    @AfterAll
    static void tearDownAll() {
        // Restore original file
        File test = new File(testFile);
        if (test.exists()) {
            test.delete();
        }
        File originalBackup = new File(originalFile + ".bak");
        if (originalBackup.exists()) {
            originalBackup.renameTo(new File(originalFile));
        }
    }

    @BeforeEach
    void setUp() {
        // Initialize test player with sample data
        testPlayer = new Player("TestPlayer", 5, 123456);
        testPlayer.addWin(TIC_TAC_TOE);
        testPlayer.addLoss(CONNECT_FOUR);
        testPlayer.setMMR(1500, CHECKERS);
        testPlayer.setGameSignal(1, TIC_TAC_TOE);
        testPlayer.setRank(new Rank(RankTier.SILVER), TIC_TAC_TOE);

        // Ensure clean state before each test
        PlayerDatabase.loadPlayersFromCSV();
        PlayerDatabase.getAllPlayers().clear();
    }



    @Test
    void testSaveAndLoadPlayer() {
        // Save the player
        assertTrue(PlayerDatabase.savePlayer(testPlayer));

        // Load players from file
        PlayerDatabase.loadPlayersFromCSV();

        // Retrieve the player
        Player loadedPlayer = PlayerDatabase.getPlayerByUserID(123456);
        assertNotNull(loadedPlayer, "Player should be loaded from database");

        // Verify all loaded data
        assertEquals("TestPlayer", loadedPlayer.getUsername());
        assertEquals(5, loadedPlayer.getLevel());
        assertEquals(123456, loadedPlayer.getUserID());

        // Verify game-specific data
        assertEquals(1, loadedPlayer.getWins(TIC_TAC_TOE));
        assertEquals(0, loadedPlayer.getLosses(TIC_TAC_TOE));
        assertEquals(1.0, loadedPlayer.getWinRatio(TIC_TAC_TOE));

        assertEquals(0, loadedPlayer.getWins(CONNECT_FOUR));
        assertEquals(1, loadedPlayer.getLosses(CONNECT_FOUR));
        assertEquals(0.0, loadedPlayer.getWinRatio(CONNECT_FOUR));

        assertEquals(0, loadedPlayer.getWins(CHECKERS));
        assertEquals(0, loadedPlayer.getLosses(CHECKERS));
        assertEquals(0.0, loadedPlayer.getWinRatio(CHECKERS));

        assertEquals(1500, loadedPlayer.getMMR(CHECKERS));
        assertEquals(1, loadedPlayer.getGameSignal(TIC_TAC_TOE));
        assertEquals(100, loadedPlayer.getRank(TIC_TAC_TOE).getRankingPoints());
    }

    @Test
    void testUpdateExistingPlayer() {
        // Initial save
        assertTrue(PlayerDatabase.savePlayer(testPlayer));

        // Modify player data
        testPlayer.setLevel(10);
        testPlayer.addWin(TIC_TAC_TOE);
        testPlayer.setMMR(1600, CHECKERS);
        testPlayer.setGameSignal(2, CONNECT_FOUR);
        testPlayer.setRank(new Rank(RankTier.SILVER), CONNECT_FOUR);

        // Save updates
        assertTrue(PlayerDatabase.savePlayer(testPlayer));

        // Reload and verify
        PlayerDatabase.loadPlayersFromCSV();
        Player updatedPlayer = PlayerDatabase.getPlayerByUserID(123456);

        assertEquals(10, updatedPlayer.getLevel());
        assertEquals(2, updatedPlayer.getWins(TIC_TAC_TOE));
        assertEquals(1600, updatedPlayer.getMMR(CHECKERS));
        assertEquals(2, updatedPlayer.getGameSignal(CONNECT_FOUR));
        assertEquals(200, updatedPlayer.getRank(CONNECT_FOUR).getRankingPoints());
    }

    @Test
    void testDeletePlayer() {
        // Save first
        assertTrue(PlayerDatabase.savePlayer(testPlayer));
        assertNotNull(PlayerDatabase.getPlayerByUserID(123456), "Player should exist before deletion");

        // Delete and verify
        assertTrue(PlayerDatabase.deletePlayer(123456));
        assertNull(PlayerDatabase.getPlayerByUserID(123456), "Player should be deleted");

        // Verify file is updated
        PlayerDatabase.loadPlayersFromCSV();
        assertNull(PlayerDatabase.getPlayerByUserID(123456), "Player should remain deleted after reload");
    }

    @Test
    void testNonexistentPlayerOperations() {
        assertNull(PlayerDatabase.getPlayerByUserID(999999));
        assertNull(PlayerDatabase.getPlayerByUsername("Nonexistent"));
    }

    @Test
    void testMultiplePlayerOperations() {
        Player player2 = new Player("Player2", 3, 654321);
        player2.addWin(CONNECT_FOUR);
        player2.setMMR(1200, CONNECT_FOUR);

        assertTrue(PlayerDatabase.savePlayer(testPlayer));
        assertTrue(PlayerDatabase.savePlayer(player2));

        List<Player> allPlayers = PlayerDatabase.getAllPlayers();
        assertEquals(2, allPlayers.size());

        Player loaded1 = PlayerDatabase.getPlayerByUserID(123456);
        Player loaded2 = PlayerDatabase.getPlayerByUserID(654321);

        assertNotNull(loaded1);
        assertNotNull(loaded2);
        assertEquals("TestPlayer", loaded1.getUsername());
        assertEquals("Player2", loaded2.getUsername());
        assertEquals(1200, loaded2.getMMR(CONNECT_FOUR));
    }

    @Test
    void testGetPlayerByUsername() {
        assertTrue(PlayerDatabase.savePlayer(testPlayer));

        Player foundPlayer = PlayerDatabase.getPlayerByUsername("TestPlayer");
        assertNotNull(foundPlayer);
        assertEquals(123456, foundPlayer.getUserID());

        // Test case sensitivity
        assertNotNull(PlayerDatabase.getPlayerByUsername("testplayer"));
        assertNull(PlayerDatabase.getPlayerByUsername("unknown"));
    }

}