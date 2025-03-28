package networking.game;

import java.util.HashMap;

public class ScoreValidator  {


    public static boolean isScoreValid(String gameID, String playerId , int score, HashMap<String, Integer> playerScore, HashMap<String, HashMap<String, Integer>> gameScore) {
        if(gameScore.containsKey(gameID) && (gameScore.get(gameID) == playerScore && playerScore.containsKey(playerId)) && playerScore.get(playerId) == score) {
            System.out.println("Score already exists for player " + playerId);
            return false;
        }else {
            if(score <0 || score > 1) {
                System.out.println("Error: Score must be between 0 and 1");
                return false;
            }
            else{
                return true;
            }
        }

    }
}
