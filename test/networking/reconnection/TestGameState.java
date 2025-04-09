package networking.reconnection;

/**
 * Test implementation of GameState.
 */
class TestGameState extends GameState {
    private final String state;

    public TestGameState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }
}
