package MatchmakingLeaderboard;

/**
 * Enum representing different rank tiers.
 */
public enum RankTier {
    BRONZE(0, "BRONZE"),
    SILVER(1000, "SILVER"),
    GOLD(2000, "GOLD"),
    DIAMOND(3000, "DIAMOND");

    private final int thresholdPoints;
    private final String rankName;

    RankTier(int thresholdPoints, String rankName) {
        this.thresholdPoints = thresholdPoints;
        this.rankName = rankName;
    }

    public int getThresholdPoints() {
        return this.thresholdPoints;
    }

    public String getRankName() {
        return this.rankName;
    }

    public RankTier getNextTier(){
        int nextTier = this.ordinal() + 1;
        if(nextTier < RankTier.values().length){
            return RankTier.values()[nextTier];
        }
        return null;
    }

    public RankTier getPreviousTier(){
        int previousTier = this.ordinal() - 1;
        if(previousTier >= 0){
            return RankTier.values()[previousTier];
        }
        return null;
    }

}