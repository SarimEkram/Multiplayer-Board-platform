package MatchmakingLeaderboard.TicTacToe.Leaderboard;

public class LeaderboardNetworkFailureException extends Exception{

    public LeaderboardNetworkFailureException(){
        super("A Network Roadblock was encountered. Leaderboard Unavailable");
    }
}
