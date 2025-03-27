package MatchmakingLeaderboard;

import MatchmakingLeaderboard.Checkers.Matchmaking.CheckersMatchmaking;
import MatchmakingLeaderboard.Connect4.Matchmaking.Connect4Matchmaking;
import MatchmakingLeaderboard.TicTacToe.Matchmaking.TicTacToeMatchmaking;

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
    private Rank rank;
    private int gameSignal;

    /**
     * construct class for Player
     * @param winRatio win ratio of player
     * @param level rank level of player
     * @param userID userID of the player from database
     */
    public Player(double winRatio, int level, int userID, boolean spectate, Rank rank, int gameSignal ) {
        this.winRatio = winRatio;
        this.level = level;
        this.userID = userID;
        this.spectate = spectate;
        this.rank = rank;
        this.gameSignal = gameSignal;
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

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public int getGameSignal() {
        return gameSignal;
    }

    public void setGameSignal(int gameSignal) {
        this.gameSignal = gameSignal;
    }

    /**
     * function to
     */
    public void joinMatch() throws Exception {
        if (this.getGameSignal() == 1){
            TicTacToeMatchmaking matchmaking = new TicTacToeMatchmaking();
            matchmaking.joinQueue(this);
            matchmaking.startMatchmaking();
        } else if (this.getGameSignal() == 2){
            Connect4Matchmaking matchmaking = new Connect4Matchmaking();
            matchmaking.joinQueue(this);
            matchmaking.startMatchmaking();
        } else if (this.getGameSignal() == 3) {
            CheckersMatchmaking matchmaking = new CheckersMatchmaking();
            matchmaking.joinQueue(this);
            matchmaking.startMatchmaking();
        }
    }

    public void cancelMatch(){

    }

    public void spectateMatch(int gameid){
    }
}
