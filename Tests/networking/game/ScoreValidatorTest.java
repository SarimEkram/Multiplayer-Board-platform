package networking.game;

import networking.game.ScoreValidator;
import org.junit.jupiter.api.*;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreValidatorTest {

    @Test
    void testValidScore_NewEntry() {
        String gameID = "game1";
        String playerId = "player1";
        int score = 1;
        HashMap<String, Integer> playerScore = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> gameScore = new HashMap<>();
        gameScore.put(gameID, playerScore);

        boolean result = ScoreValidator.isScoreValid(gameID, playerId, score, playerScore, gameScore);
        assertTrue(result);
    }

    @Test
    void testScoreAlreadyExists() {
        String gameID = "game1";
        String playerId = "player1";
        int score = 1;

        HashMap<String, Integer> playerScore = new HashMap<>();
        playerScore.put(playerId, score);

        HashMap<String, HashMap<String, Integer>> gameScore = new HashMap<>();
        gameScore.put(gameID, playerScore);

        boolean result = ScoreValidator.isScoreValid(gameID, playerId, score, playerScore, gameScore);
        assertFalse(result);
    }

    @Test
    void testInvalidNegativeScore() {
        String gameID = "game1";
        String playerId = "player1";
        int score = -1;

        HashMap<String, Integer> playerScore = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> gameScore = new HashMap<>();
        gameScore.put(gameID, playerScore);

        boolean result = ScoreValidator.isScoreValid(gameID, playerId, score, playerScore, gameScore);
        assertFalse(result);
    }

    @Test
    void testInvalidScoreAboveOne() {
        String gameID = "game1";
        String playerId = "player1";
        int score = 2;

        HashMap<String, Integer> playerScore = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> gameScore = new HashMap<>();
        gameScore.put(gameID, playerScore);

        boolean result = ScoreValidator.isScoreValid(gameID, playerId, score, playerScore, gameScore);
        assertFalse(result);
    }

    @Test
    void testGameIdNotInGameScore() {
        String gameID = "nonexistentGame";
        String playerId = "player1";
        int score = 1;

        HashMap<String, Integer> playerScore = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> gameScore = new HashMap<>();

        boolean result = ScoreValidator.isScoreValid(gameID, playerId, score, playerScore, gameScore);
        assertFalse(result);
    }

    @Test
    void testPlayerScoreReferenceMismatch() {
        String gameID = "game1";
        String playerId = "player1";
        int score = 1;

        HashMap<String, Integer> playerScore1 = new HashMap<>();
        HashMap<String, Integer> playerScore2 = new HashMap<>();
        playerScore1.put(playerId, score);

        HashMap<String, HashMap<String, Integer>> gameScore = new HashMap<>();
        gameScore.put(gameID, playerScore1);

        boolean result = ScoreValidator.isScoreValid(gameID, playerId, score, playerScore2, gameScore);
        assertTrue(result);
    }
    @Test
    void testInValidNumberOfPlayers() {
        String gameID = "game1";
        String playerId1 = "player1";
        int score = 1;
        String playerId2 = "player2";
        int score2 = 0;
        String playerId3 = "player3";
        int score3 = 1;


        HashMap<String, Integer> playerScore = new HashMap<>();
        playerScore.put(playerId1, score);
        playerScore.put(playerId2, score2);
        HashMap<String, HashMap<String, Integer>> gameScore = new HashMap<>();
        gameScore.put(gameID, playerScore);

        boolean result = ScoreValidator.isScoreValid(gameID, playerId3, score3, playerScore, gameScore);
        assertFalse(result);
    }
    @Test
    void testValidNumberOfPlayers() {
        String gameID = "game1";
        String playerId1 = "player1";
        int score = 1;
        String playerId2 = "player2";
        int score2 = 0;

        HashMap<String, Integer> playerScore = new HashMap<>();
        playerScore.put(playerId1, score);
        HashMap<String, HashMap<String, Integer>> gameScore = new HashMap<>();
        gameScore.put(gameID, playerScore);

        boolean result = ScoreValidator.isScoreValid(gameID, playerId2, score2, playerScore, gameScore);
        assertTrue(result);
    }


}
