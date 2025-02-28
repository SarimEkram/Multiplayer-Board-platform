package MatchmakingLeaderboard;

/**
 * Represents a player's rank based on ranking points.
 */
public class Rank {
    private Player player;
    private int rankingPoints;
    private RankTier rankTier;

    /**
     * Constructor initializes player's rank based on points
     */
    public Rank(Player player) {

    }

    /**
     * Calculates the rank tier based on points
     * @return player's rank
     */
    private String calculateRankTier() {
        return BRONZE; // Default rank will be bronze
    }

    /**
     * Updates the ranking points and recalculates the rank
     */
    public void updateRank(int pointsEarned) {
    }

    /**
     * Getter method for recieving points
     * @return The player's ranking points
     */
    public int getRankingPoints() {
        return rankingPoints;
    }

    /**
     * Getter method for recieving rank
     * @return rank of the player
     */
    public String getRankTier() {
        return rankTier;
    }

    @Override
    public String toString() {
        return "something";
    }
}
