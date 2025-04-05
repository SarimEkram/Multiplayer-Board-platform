package networking.game;

import java.util.HashMap;

/**
 * The ScoreValidator class provides a method to validate player scores
 * within a game session.
 */

public class ScoreValidator  {

    /**
     * Validates whether a given score for a player in a game is acceptable.
     *
     * @param gameID       The unique identifier for the game session.
     * @param playerId     The unique identifier for the player.
     * @param score        The score to be validated.
     * @param playerScore  The map containing player scores for a specific game.
     * @param gameScore    The map containing scores for all games.
     * @return             {@code true} if the score is valid, {@code false} otherwise.
     */


    public static boolean isScoreValid(String gameID, String playerId , int score, HashMap<String, Integer> playerScore, HashMap<String, HashMap<String, Integer>> gameScore) {
        if(gameScore.containsKey(gameID) && gameScore.get(gameID).containsValue(playerScore.get(playerId)) && playerScore.containsKey(playerId) && playerScore.get(playerId) == score) { // checking if player score already exists
            System.out.println("Score already exists for player " + playerId); // error message
            return false;
        }else {
            if(score <0 || score > 1 || !gameScore.containsKey(gameID) ){ // checking if score is between 0(lose) and 1(win)
                System.out.println("Error: Score must be between 0 and 1");
                return false;
            }
            else{
                return true;
            }
        }

    }
}
