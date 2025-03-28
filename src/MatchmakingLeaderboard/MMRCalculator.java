package MatchmakingLeaderboard;

public class MMRCalculator {
    private static final int MMR_gain = 25;
    private static final double MMR_loss = 12.5;
    private static final int MMR_draw = 2;

    /**
     * Calculates the MMR change for a player after a win or loss.
     * Uses the win ratio for the specific game to compare skill levels.
     *
     * @param p1         The player whose MMR is being calculated
     * @param p2         The opponent player
     * @param won        True if p1 won the match, false if p1 lost
     * @param gameSignal The game ID (1: TicTacToe, 2: Connect4, 3: Checkers)
     * @return MMR points to adjust (positive for win, negative for loss)
     */
    public static int calculateMMR(Player p1, Player p2, boolean won, int gameSignal) {
        double skillDifference = p1.getWinRatio(gameSignal) - p2.getWinRatio(gameSignal);

        double modifier = 1 + (skillDifference * 0.5);
        if (modifier < 0.5) modifier = 0.5;
        if (modifier > 2.0) modifier = 2.0;

        if (won) {
            return (int) (MMR_gain * modifier);
        } else {
            return -(int) (MMR_loss * modifier);
        }
    }

    /**
     * Calculates the MMR gain after a draw based on skill similarity.
     *
     * @param p1         First player
     * @param p2         Second player
     * @param gameSignal The game ID to use correct win ratio
     * @return MMR points to be added after a draw
     */
    public static int calculateDraw(Player p1, Player p2, int gameSignal) {
        double skillDifference = Math.abs(p1.getWinRatio(gameSignal) - p2.getWinRatio(gameSignal));

        double modifier = 1.0 - skillDifference;
        if (modifier < 0.2) modifier = 0.2;
        if (modifier > 1.0) modifier = 1.0;

        return (int) (MMR_draw * modifier);
    }
}
