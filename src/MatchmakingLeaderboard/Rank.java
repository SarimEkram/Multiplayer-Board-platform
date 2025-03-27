package MatchmakingLeaderboard;


/**
 * Represents a player's rank based on ranking points.
 */
public class Rank {
    private int rankingPoints;
    private RankTier currentTier;

    public Rank(){
        this(0);
    }
    /**
     * Constructor initializes player's rank based on points
     */
    public Rank(int Points) {
        this.rankingPoints = Math.max(0, Points);
        updateRankTier();
    }

    /**
     * Adjusts points and updates tier
     */
    public void adjustPoints(int points) {
        this.rankingPoints += points;
        this.rankingPoints = Math.max(0, this.rankingPoints);
        updateRankTier();
    }
    /**
     * updates the rank tier based on points
     */
    private void updateRankTier() {

        if (rankingPoints >= RankTier.DIAMOND.getThresholdPoints()){
            currentTier = RankTier.DIAMOND;
        }
        else if (rankingPoints >= RankTier.GOLD.getThresholdPoints()) {
            currentTier = RankTier.GOLD;
        }
        else if (rankingPoints >= RankTier.SILVER.getThresholdPoints()){
            currentTier = RankTier.SILVER;
        }
        else {
            currentTier = RankTier.BRONZE;
        }

    }

    /**
     * Getter method for receiving points
     * @return The player's ranking points
     */
    public int getRankingPoints() {
        return rankingPoints;
    }

    /**
     * Getter method for receiving rank
     * @return rank of the player
     */
    public RankTier getCurrentTier() {
        return this.currentTier;
    }

    @Override
    public String toString() {
        return "something";
    }
}
