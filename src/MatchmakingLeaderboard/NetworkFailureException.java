package MatchmakingLeaderboard;

import java.io.IOException;

public class NetworkFailureException extends IOException {

    public NetworkFailureException(String str){
        super(str);
    }
}
