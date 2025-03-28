package MatchmakingLeaderboard;

import MatchmakingLeaderboard.Checkers.Matchmaking.CheckersMatchmaking;
import MatchmakingLeaderboard.Connect4.Matchmaking.Connect4Matchmaking;
import MatchmakingLeaderboard.TicTacToe.Matchmaking.TicTacToeMatchmaking;

/**
 * Player class that is used to represent Users pulled from database
 * and used in matchmaking and leaderboard
 */
public class Player {
    private double[] winRatio = new double[3];  // 0: TicTacToe, 1: Connect4, 2: Checkers
    private int level;
    private int userID;
    private boolean spectate;
    private Rank rank;
    private int gameSignal;
    private int mmr;

    /**
     * Constructs a Player object
     * @param level rank level of player
     * @param userID userID of the player from database
     * @param spectate whether player is spectating
     * @param rank rank of the player
     * @param gameSignal which game the player wants to play
     */
    public Player(double winRatioForGame, int level, int userID, boolean spectate, Rank rank, int gameSignal) {
        this.level = level;
        this.userID = userID;
        this.spectate = spectate;
        this.rank = rank;
        this.gameSignal = gameSignal;
        this.setWinRatio(gameSignal, winRatioForGame);
    }


    // --- MMR ---
    public int getMMR(){
        return mmr;
    }

    public void setMMR(int mmr){
        this.mmr = mmr;
    }

    // --- Win Ratio ---
    public double getWinRatio(int gameSignal) {
        int index = gameSignal - 1;
        return (index >= 0 && index < winRatio.length) ? winRatio[index] : 0.0;
    }

    public void setWinRatio(int gameSignal, double ratio) {
        int index = gameSignal - 1;
        if (index >= 0 && index < winRatio.length) {
            winRatio[index] = ratio;
        }
    }

    // --- General Info ---
    public boolean isSpectate() {
        return this.spectate;
    }

    public void setSpectate(boolean spectate) {
        this.spectate = spectate;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getUserID() {
        return this.userID;
    }

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
     * Joins the player into matchmaking queue based on their game signal
     * @throws Exception if there's an error during matchmaking
     */
    public void joinMatch() throws Exception {
        if (this.getGameSignal() == 1) {
            TicTacToeMatchmaking matchmaking = new TicTacToeMatchmaking();
            matchmaking.joinQueue(this);
            matchmaking.startMatchmaking();
        } else if (this.getGameSignal() == 2) {
            Connect4Matchmaking matchmaking = new Connect4Matchmaking();
            matchmaking.joinQueue(this);
            matchmaking.startMatchmaking();
        } else if (this.getGameSignal() == 3) {
            CheckersMatchmaking matchmaking = new CheckersMatchmaking();
            matchmaking.joinQueue(this);
            matchmaking.startMatchmaking();
        }
    }

    /**
     * Cancels the current matchmaking search
     */
    public void cancelMatch() {
        // Implementation needed
    }

    /**
     * Allows player to spectate a specific game
     * @param gameid the ID of the game to spectate
     */
    public void spectateMatch(int gameid) {
        // Implementation needed
    }
}
