package networking.reconnection;

/**
 * Represents a basic game state for a player.
 */
public class GameState {
    private int score;
    private int level;

    public GameState(int score, int level) {
        this.score = score;
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "[score: " + score + ", level: " + level + "]";
    }
}
