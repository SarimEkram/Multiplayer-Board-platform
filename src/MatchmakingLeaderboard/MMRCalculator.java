package MatchmakingLeaderboard;

public class MMRCalculator {
    private static final int MMR_gain = 25;
    private static final double MMR_loss = 12.5;
    private static final int MMR_draw = 2;


    /**
     * Calculates the MMR change for a player after a win or loss.
     * The MMR adjustment depends on the win ratio difference between players.
     * <p>
     * - If a lower-skilled player (based on win ratio) wins, they gain more MMR.
     * - If a higher-skilled player wins, they gain less MMR.
     * - Similarly, losing to a much weaker player results in a larger MMR loss.
     *
     * @param p1  The player whose MMR is being calculated
     * @param p2  The opponent player
     * @param won True if p1 won the match, false if p1 lost
     * @return MMR points to adjust (positive for win, negative for loss)
     */
    public static int calculateMMR(Player p1, Player p2, boolean won) {
        double skillDifference = p1.getWinRatio() - p2.getWinRatio();

        // Apply a simple modifier based on skill gap
        double modifier = 1 + (skillDifference * 0.5); // You can tweak the 0.5 factor

        if (modifier < 0.5) modifier = 0.5; // prevent super low MMR change
        if (modifier > 2.0) modifier = 2.0; // prevent extreme gain/loss

        if (won) {
            return (int) (MMR_gain * modifier);
        } else {
            return -(int) (MMR_loss * modifier); // negative value for loss
        }
    }

    /**
     * Calculates the MMR change for a player after a draw.
     * The MMR gain is based on how close the players are in skill level.
     * <p>
     * - If players have similar win ratios, they gain more MMR.
     * - If there’s a big skill gap, the gain is reduced.
     *
     * @param p1 First player
     * @param p2 Second player
     * @return MMR points to be added after a draw
     */
    public static int calculateDraw(Player p1, Player p2) {
        double skillDifference = Math.abs(p1.getWinRatio() - p2.getWinRatio());

        // The closer the skill, the more fair the draw
        // Less difference = higher reward for both
        double modifier = 1.0 - skillDifference; // closer = bigger number

        if (modifier < 0.2) modifier = 0.2; // prevent zero MMR gain
        if (modifier > 1.0) modifier = 1.0;

        return (int) (MMR_draw * modifier);
    }
}
