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


    // Reset the timer when a player successfully makes a move
    public void resetTimer() {
        this.remainingTime = turnDuration * 1000L;
        System.out.println("Timer reset for player: " + playerId);
    }

    // Pause the timer in case of network issues or reconnection
    public void pauseTimer() {
        if (!isPaused) {
            remainingTime -= (System.currentTimeMillis() - startTime);
            isPaused = true;
            System.out.println("Timer paused for player: " + playerId);
        }
    }


    // Resume the timer after a network issue is resolved
    public void resumeTimer() {
        if (isPaused) {
            startTime = System.currentTimeMillis();
            isPaused = false;
            System.out.println("Timer resumed for player: " + playerId);
        }
    }

    // Check if the player's turn has expired
    public boolean isTimeExpired() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        return elapsedTime >= remainingTime;
    }

    // Handle player disconnection when the timer expires
    public void handleTimerExpiry() {
        if (isTimeExpired()) {
            System.out.println("Player " + playerId + " timed out! Disconnecting...");
            disconnectPlayer();
        }
    }

    // Notify player when their time is about to expire
    public void notifyPlayer() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (remainingTime - elapsedTime <= 10000) { // 10 seconds remaining
            System.out.println("Warning: 10 seconds left for player " + playerId);
            sendWarningNotification();
        }
    }

    // Simulated method for disconnecting the inactive player via the networking system
    private void disconnectPlayer() {
        // TODO: Implement network call to remove the player from the game session
        System.out.println("Network: Player " + playerId + " removed from session.");
    }

    // Simulated method for sending a warning notification to the player
    private void sendWarningNotification() {
        // TODO: Implement server message or client UI alert
        System.out.println("Network: Warning sent to player " + playerId);
    }







}
