package MatchmakingLeaderboard;

public class MMRCalculator {
    private static final int MMR_gain = 25;
    private static final double MMR_loss = 12.5;
    private static final int MMR_draw = 2;

    public static int calculateMMR(Player p1, Player p2, boolean Won){
        int mmrUpdate;

        if(Won){
            mmrUpdate = MMR_gain;
            double skillDifference = p1.getWinRatio() - p2.getWinRatio();
        }
        return 1;
    }

    public static int calculateDraw(Player p1, Player p2){
        return 5;
    }
}
