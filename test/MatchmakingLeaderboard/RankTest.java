package MatchmakingLeaderboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RankTest {

    private Rank rank;
    private Player player;

    @BeforeEach
    void setUp() {
        rank = new Rank(); // starts at BRONZE by default
        player = new Player("Jay", 1, 123456);
        for (GameType game : GameType.values()) {
            player.setRank(new Rank(), game); // ensure rank is initialized for each game
        }
    }

    /**
     * Test that a new Rank instance starts at BRONZE tier with 0 points.
     */
    @Test
    void testDefaultConstructor() {
        assertEquals(0, rank.getRankingPoints());
        assertEquals(RankTier.BRONZE, rank.getCurrentTier());
    }

    /**
     * Test that providing a tier to the constructor sets the tier correctly.
     */
    @Test
    void testConstructorWithTier() {
        Rank bronzeRank = new Rank(RankTier.BRONZE);
        assertEquals(RankTier.BRONZE, bronzeRank.getCurrentTier());
    }

    /**
     * Test that points increase correctly when adjusted positively.
     */
    @Test
    void testAdjustPointsIncrease() {
        player.getRank(GameType.TIC_TAC_TOE).adjustPoints(player, 60, GameType.TIC_TAC_TOE);
        assertEquals(60, player.getRank(GameType.TIC_TAC_TOE).getRankingPoints());
    }

    /**
     * Test that points do not go below 0 when reduced.
     */
    @Test
    void testAdjustPointsCannotGoNegative() {
        player.getRank(GameType.TIC_TAC_TOE).adjustPoints(player, -50, GameType.TIC_TAC_TOE);
        assertEquals(0, player.getRank(GameType.TIC_TAC_TOE).getRankingPoints());
    }

    /**
     * Test that tier upgrades as ranking points increase.
     */
    @Test
    void testTierUpgradeThroughPlayer() {
        Rank rank = player.getRank(GameType.TIC_TAC_TOE);

        // BRONZE → SILVER
        rank.adjustPoints(player, RankTier.SILVER.getThresholdPoints(), GameType.TIC_TAC_TOE);
        assertEquals(RankTier.SILVER, rank.getCurrentTier());

        // SILVER → GOLD
        rank.adjustPoints(player, RankTier.GOLD.getThresholdPoints() - RankTier.SILVER.getThresholdPoints(), GameType.TIC_TAC_TOE);
        assertEquals(RankTier.GOLD, rank.getCurrentTier());

        // GOLD → DIAMOND
        rank.adjustPoints(player, RankTier.DIAMOND.getThresholdPoints() - RankTier.GOLD.getThresholdPoints(), GameType.TIC_TAC_TOE);
        assertEquals(RankTier.DIAMOND, rank.getCurrentTier());
    }

    /**
     * Test that tier downgrades when points fall below threshold.
     */
    @Test
    void testTierDowngradeThroughPlayer() {
        Rank rank = player.getRank(GameType.TIC_TAC_TOE);

        // First go to DIAMOND
        rank.adjustPoints(player, RankTier.DIAMOND.getThresholdPoints(), GameType.TIC_TAC_TOE);
        assertEquals(RankTier.DIAMOND, rank.getCurrentTier());

        // Drop to GOLD
        rank.adjustPoints(player, - (RankTier.DIAMOND.getThresholdPoints() - RankTier.GOLD.getThresholdPoints()), GameType.TIC_TAC_TOE);
        assertEquals(RankTier.GOLD, rank.getCurrentTier());

        // Drop to SILVER
        rank.adjustPoints(player, - (RankTier.GOLD.getThresholdPoints() - RankTier.SILVER.getThresholdPoints()), GameType.TIC_TAC_TOE);
        assertEquals(RankTier.SILVER, rank.getCurrentTier());

        // Drop to BRONZE
        rank.adjustPoints(player, - (RankTier.SILVER.getThresholdPoints()), GameType.TIC_TAC_TOE);
        assertEquals(RankTier.BRONZE, rank.getCurrentTier());
    }

}
