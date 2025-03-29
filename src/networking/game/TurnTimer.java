package networking.game;

public class TurnTimer {
    private int turnDuration; // Time limit per turn in seconds
    private boolean isPaused;
    private long startTime;
    private long remainingTime;
    private String playerId; // Unique identifier for the player


    public TurnTimer(String playerId, int duration) {
        this.playerId = playerId;
        this.turnDuration = duration;
        this.isPaused = false;
        this.remainingTime = duration * 1000L;
    }

    // Start the timer when the player's turn begins
    public void startTimer() {
        this.startTime = System.currentTimeMillis();
        this.isPaused = false;
        System.out.println("Turn timer started for player: " + playerId);
    }









}
