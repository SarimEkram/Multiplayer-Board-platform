package MatchmakingLeaderboard;

/**
 * Player class that is used to represent Users pulled from database and used in matchmaking and leaderboard
 *
 * @author Manav Patel
 */
public class Player {
    private double winRatio;
    private int level;
    private int userID;

    /**
     * construct class for Player
     * @param winRatio win ratio of player
     * @param level rank level of player
     * @param userID userID of the player from database
     */
    public Player(double winRatio, int level, int userID) {
        this.winRatio = winRatio;
        this.level = level;
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
    public int getlevel() {
        return this.level;
    }

    /**
     * set level
     */
    public void setlevel(int level) {
        this.level = level;
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
