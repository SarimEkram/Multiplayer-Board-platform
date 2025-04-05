package MatchmakingLeaderboard;

/**
 * Enum for different game types within the matchmaking leaderboard system.
 */
public enum GameType {
    TIC_TAC_TOE(1),
    CONNECT_FOUR(2),
    CHECKERS(3);

    private final int gameCode;

    GameType(int gameCode) {
        this.gameCode = gameCode;
    }

    public int getGameCode() {
        return this.gameCode;
    }
}
