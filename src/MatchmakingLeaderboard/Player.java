package MatchmakingLeaderboard;

import MatchmakingLeaderboard.Checkers.Matchmaking.CheckersMatchmaking;
import MatchmakingLeaderboard.Connect4.Matchmaking.Connect4Matchmaking;
import MatchmakingLeaderboard.TicTacToe.Matchmaking.TicTacToeMatchmaking;
//import MatchmakingLeaderboard.Rank;
/**
 * Player class that is used to represent Users pulled from database
 * and used in matchmaking and leaderboard
 */
public class Player {
    private final double[] winRatio = new double[3];  // 1: TicTacToe, 2: Connect4, 3: Checkers
    private final int[] wins = new int[3];
    private final int[] losses = new int[3];
    private int level;
    private int userID;
    private boolean spectate;
    private final Rank[] rank = new Rank[3];
    private final int[] gameSignal = new int[3];
    private final int[] mmr = new int[3];

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
        for(int i = 0; i < 3; i++) {
            this.winRatio[i] = 0.0;
            this.wins[i] = 0;
            this.losses[i] = 0;
            this.rank[i] = new Rank();
            this.gameSignal[i] = 0;
            this.mmr[i] = 0;
        }

    }

    public int getWins(int gameType) {
        validGame(gameType);
        return wins[gameType-1];
    }
    public int getLosses(int gameType) {
        validGame(gameType);
        return losses[gameType-1];
    }
    private void validGame(int gameType){
        if(gameType < 1 || gameType > 3) {
            throw new IllegalArgumentException("Invalid game type");
        }
    }
    // --- MMR ---
    public int getMMR(int gameType){
        validGame(gameType);
        return mmr[gameType - 1];
    }

    public void setMMR(int mmr, int gameType){
        validGame(gameType);
        this.mmr[gameType - 1] = mmr;
    }

    // --- Win Ratio ---
    public double getWinRatio(int gameSignal) {
        validGame(gameSignal);
        return winRatio[gameSignal - 1];
    }

    public void setWinRatio(int gameSignal, double ratio) {
        validGame(gameSignal);
        winRatio[gameSignal - 1] = ratio;
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

    public Rank getRank(int gameType) {
        validGame(gameType);
        return this.rank[gameType - 1];
    }

    public void setRank(Rank rank, int gameType) {
        validGame(gameType);
        this.rank[gameType - 1] = rank;
    }

    public int getGameSignal(int gameType) {
        validGame(gameType);
        return this.gameSignal[gameType-1];
    }

    public void setGameSignal(int Signal, int gameType) {
        validGame(gameType);
        if(gameType < 0 || gameType > 3) {
            throw new IllegalArgumentException("Signal must be between 0 and 3");
        }

        gameSignal[gameType - 1] = Signal;
    }


    /**
     * Joins the player into matchmaking queue based on their game signal
     * @throws Exception if there's an error during matchmaking
     */
    public void joinMatch(int gameType) throws Exception {
        if (this.getGameSignal(gameType) == 1) {
            TicTacToeMatchmaking matchmaking = new TicTacToeMatchmaking();
            matchmaking.joinQueue(this);
            matchmaking.startMatchmaking();
        } else if (this.getGameSignal(gameType) == 2) {
            Connect4Matchmaking matchmaking = new Connect4Matchmaking();
            matchmaking.joinQueue(this);
            matchmaking.startMatchmaking();
        } else if (this.getGameSignal(gameType) == 3) {
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
