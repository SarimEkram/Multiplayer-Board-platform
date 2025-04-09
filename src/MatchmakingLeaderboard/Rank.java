package MatchmakingLeaderboard;


/**
 * Represents a player's rank based on ranking points.
 */
public class Rank {
    private int rankingPoints;
    private RankTier currentTier;

    public Rank(){
        this.currentTier = RankTier.BRONZE;
    }
    /**
     * Constructor initializes player's rank based on points
     */
    public Rank(RankTier Tier) {
        this.currentTier = Tier;
        updateRankTier();
    }

    /**
     * Adjusts points and updates tier
     */
    public void adjustPoints(Player player,int points, GameType game) {
        player.getRank(game).rankingPoints += points;
        player.getRank(game).rankingPoints = Math.max(0, player.getRank(game).rankingPoints);
        updateRankTier();
    }
    /**
     * updates the rank tier based on points
     */
    public void updateRankTier() {

        if (this.rankingPoints >= RankTier.DIAMOND.getThresholdPoints()){
            this.currentTier = RankTier.DIAMOND;
        }
        else if (this.rankingPoints >= RankTier.GOLD.getThresholdPoints()) {
            this.currentTier = RankTier.GOLD;
        }
        else if (rankingPoints >= RankTier.SILVER.getThresholdPoints()){
            this.currentTier = RankTier.SILVER;
        }
        else {
            this.currentTier = RankTier.BRONZE;
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
}

