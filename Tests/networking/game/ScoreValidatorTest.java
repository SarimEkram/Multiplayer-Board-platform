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
        assertFalse(result); // Because there's no gameID, the nested condition is skipped, only score range matters
    }

    @Test
    void testPlayerScoreReferenceMismatch() {
        String gameID = "game1";
        String playerId = "player1";
        int score = 1;

        HashMap<String, Integer> playerScore1 = new HashMap<>();
        HashMap<String, Integer> playerScore2 = new HashMap<>(); // different object
        playerScore1.put(playerId, score);

        HashMap<String, HashMap<String, Integer>> gameScore = new HashMap<>();
        gameScore.put(gameID, playerScore1);

        boolean result = ScoreValidator.isScoreValid(gameID, playerId, score, playerScore2, gameScore);
        assertTrue(result); // should be true because reference doesn't match
    }

}
