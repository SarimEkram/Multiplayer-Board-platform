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
            player.setRank(new Rank(), game); // ensure rank is initialized
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
        Rank goldRank = new Rank(RankTier.GOLD);
        assertEquals(RankTier.GOLD, goldRank.getCurrentTier());
    }

    /**
     * Test that points increase correctly when adjusted positively.
     */
    @Test
    void testAdjustPointsIncrease() {
        rank.adjustPoints(player, 60, GameType.TIC_TAC_TOE);
        assertEquals(60, player.getRank(GameType.TIC_TAC_TOE).getRankingPoints());
    }

    /**
     * Test that points do not go below 0 when reduced.
     */
    @Test
    void testAdjustPointsCannotGoNegative() {
        rank.adjustPoints(player, -50, GameType.TIC_TAC_TOE);
        assertEquals(0, player.getRank(GameType.TIC_TAC_TOE).getRankingPoints());
    }

    /**
     * Test that tier upgrades as ranking points increase.
     */
    @Test
    void testTierUpgrade() {
        // Bronze → Silver at 100
        rank.adjustPoints(player, 100, GameType.TIC_TAC_TOE);
        assertEquals(RankTier.SILVER, player.getRank(GameType.TIC_TAC_TOE).getCurrentTier());

        // Silver → Gold at 300
        rank.adjustPoints(player, 200, GameType.TIC_TAC_TOE);
        assertEquals(RankTier.GOLD, player.getRank(GameType.TIC_TAC_TOE).getCurrentTier());

        // Gold → Diamond at 600
        rank.adjustPoints(player, 300, GameType.TIC_TAC_TOE);
        assertEquals(RankTier.DIAMOND, player.getRank(GameType.TIC_TAC_TOE).getCurrentTier());
    }

    /**
     * Test that tier downgrades when points fall below threshold.
     */
    @Test
    void testTierDowngrade() {
        // Start with high points
        rank.adjustPoints(player, 600, GameType.TIC_TAC_TOE);
        assertEquals(RankTier.DIAMOND, player.getRank(GameType.TIC_TAC_TOE).getCurrentTier());

        // Reduce enough to drop to GOLD
        rank.adjustPoints(player, -100, GameType.TIC_TAC_TOE);
        assertEquals(RankTier.GOLD, player.getRank(GameType.TIC_TAC_TOE).getCurrentTier());

        // Reduce to SILVER
        rank.adjustPoints(player, -300, GameType.TIC_TAC_TOE);
        assertEquals(RankTier.SILVER, player.getRank(GameType.TIC_TAC_TOE).getCurrentTier());

        // Reduce to BRONZE
        rank.adjustPoints(player, -300, GameType.TIC_TAC_TOE);
        assertEquals(RankTier.BRONZE, player.getRank(GameType.TIC_TAC_TOE).getCurrentTier());
    }
}
