package networking.reconnection;

/**
 * Test implementation of GameState.
 */
class TestGameState extends GameState {
    private final String state;

    public TestGameState(String state) {
        super(); // Call the default constructor of GameState
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }
}