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
    private boolean spectate;

    /**
     * construct class for Player
     * @param winRatio win ratio of player
     * @param level rank level of player
     * @param userID userID of the player from database
     */
    public Player(double winRatio, int level, int userID, boolean spectate) {
        this.winRatio = winRatio;
        this.level = level;
        this.userID = userID;
        this.spectate = spectate;
    }

    public boolean isSpectate() {
        return this.spectate;
    }

    public void setSpectate(boolean spectate) {
        this.spectate = spectate;
    }

    /**
     * get win ratio
     * @return win ratio decimal
     */
    public double getWinRatio() {
        return this.winRatio;
    }

    /**
     * set win ratio
     */
    public void setWinRatio(double winRatio) {
        this.winRatio = winRatio;
    }

    /**
     * get level
     * @return level enum
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * set level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * get User ID from database
     * @return the unique ID
     */
    public int getUserID() {
        return this.userID;
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

    public void spectateMatch(int gameid){
    }
}
