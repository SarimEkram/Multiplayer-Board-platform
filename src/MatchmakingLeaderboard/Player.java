package MatchmakingLeaderboard;

import MatchmakingLeaderboard.Checkers.Matchmaking.CheckersMatchmaking;
import MatchmakingLeaderboard.Connect4.Connect4Matchmaking;
import MatchmakingLeaderboard.TicTacToe.TicTacToeMatchmaking;

/**
 * Player class that is used to represent Users pulled from database
 * and used in matchmaking and leaderboard
 *
 */
public class Player {
    private final double[] winRatio = new double[GameType.values().length];  // 1: TicTacToe, 2: Connect4, 3: Checkers
    private String username;
    private final int[] wins = new int[GameType.values().length];
    private final int[] losses = new int[GameType.values().length];
    private int level;
    private int userID;
    private final Rank[] rank = new Rank[GameType.values().length];
    private final int[] gameSignal = new int[GameType.values().length];
    private final int[] mmr = new int[GameType.values().length];

    /**
     * Constructs a Player object
     * @param level rank level of player
     * @param userID userID of the player from database
     *
     */
    public Player(String username, int level, int userID) {
        this.level = level;
        this.username = username;
        this.userID = userID;

        for(int i = 0; i < 3; i++) {
            this.winRatio[i] = 0.0;
            this.wins[i] = 0;
            this.losses[i] = 0;
            this.rank[i] = new Rank();
            this.gameSignal[i] = GameType.values()[i].getGameCode();
            this.mmr[i] = 0;
        }

    }

    // Getter - Setter for Username
    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter - Setter for Level
    public int getLevel() {
        return this.level;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    // Getter - Setter for UsrID
    public int getUserID() {
        return this.userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }

    // Getter - Setter for MMR
    public int getMMR(GameType gameType){ return this.mmr[gameType.ordinal()]; }
    public void setMMR(int mmr, GameType gameType){ this.mmr[gameType.ordinal()] = mmr; }

    // Getter - Setter for Rank
    public Rank getRank(GameType gameType) { return this.rank[gameType.ordinal()]; }
    public void setRank(Rank rank, GameType gameType) { this.rank[gameType.ordinal()] = rank; }

    // Getters for Wins/Losses
    public int getWins(GameType gameType) { return this.wins[gameType.ordinal()]; }
    public int getLosses(GameType gameType) { return this.losses[gameType.ordinal()]; }

    // Setter for Wins/Losses
    public void addWin(GameType gameType) {
        this.wins[gameType.ordinal()]++;
        calculateRatio(gameType);
    }
    public void addLoss(GameType gameType) {
        this.losses[gameType.ordinal()]++;
        calculateRatio(gameType);
    }

    /**
     * Calculates win ratio
     * @param gameType
     */
    public void calculateRatio(GameType gameType) {
        //validGame(gameType);
        int gameIndex = gameType.ordinal() ;
        int totalGames = this.wins[gameIndex] + this.losses[gameIndex];

        if(totalGames > 0){
            setWinRatio (gameType,(double) wins[gameIndex] / totalGames);
        }
        else{
            setWinRatio(gameType, 0.0);
        }


    }

    // Getter - Setter for WinRatio
    public double getWinRatio(GameType gameType) { return this.winRatio[gameType.ordinal()]; }
    public void setWinRatio(GameType gameType, double ratio) { this.winRatio[gameType.ordinal()] = ratio; }

    // Getter - Setter for Game Signal
    public int getGameSignal(GameType gameType) {
        return this.gameSignal[gameType.ordinal()];
    }
    public void setGameSignal(int Signal, GameType gameType) {
        if(Signal < 0 || Signal > 3) {
            throw new IllegalArgumentException("Signal must be between 0 and 3");
        }
        this.gameSignal[gameType.ordinal()] = Signal;
    }

    /**
     * Joins the player into matchmaking queue based on their game signal
     * @throws Exception if there's an error during matchmaking
     */
    public void joinMatch(GameType gameType) throws Exception {
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

}

