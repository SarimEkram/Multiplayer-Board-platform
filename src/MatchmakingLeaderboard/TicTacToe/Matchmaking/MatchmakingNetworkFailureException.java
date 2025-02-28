package MatchmakingLeaderboard.TicTacToe.Matchmaking;

public class MatchmakingNetworkFailureException extends Exception{

    public MatchmakingNetworkFailureException(){
        super("A Network Roadblock was encountered. Matchmaking cancelled");
    }
}
