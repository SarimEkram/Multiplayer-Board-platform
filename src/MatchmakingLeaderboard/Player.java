package MatchmakingLeaderboard;

/**
 *
 *
 * @author Manav Patel
 */
public class Player {
    private double winRatio;
    private int Level;
    private int userID;

    /**
     * construct class for Player
     * @param winRatio win ratio of player
     * @param level rank level of player
     * @param userID userID of the player from database
     */
    public Player(double winRatio, int level, int userID) {
        this.winRatio = winRatio;
        Level = level;
        this.userID = userID;
    }

    /**
     * get win ratio
     * @return win ratio decimal
     */
    public double getWinRatio() {
        return winRatio;
    }

    /**
     * set win ratio
     */
    public void setWinRatio(int winRatio) {
        this.winRatio = winRatio;
    }

    /**
     * get level
     * @return level enum
     */
    public int getLevel() {
        return Level;
    }

    /**
     * set level
     */
    public void setLevel(int level) {
        Level = level;
    }

    /**
     * get User ID from database
     * @return the unique ID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * set User ID
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * function to
     */
    public void joinMatch(){
    }

    public void cancelMatch(){
    }
}
