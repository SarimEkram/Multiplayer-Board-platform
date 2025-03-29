package Authentication;

/**
 * Represents a user in system
 * Each user has an ID, username, email, hashed password, game stats and online status
 * All user data is persisted to the CSV database automatically when modified
 */
public class User {
    private int userID;                            // Unique identifier for user
    private String username;                       // User's Display name
    private String email;                          // User's email address
    private String password;                       // User's password (hashed)
    private double winRatio;                       // User's game ratio
    private int level;                             // User's current game level
    private boolean onlineStatus;                  // User's status
    private boolean suspendSave = false;           // Prevents auto-saving during batch updates

    public User() {}

    /**
     * Creates a fully-initialized user with all fields set
     * @param userID Unique ID
     * @param username User's username
     * @param email User's email
     * @param password Hashed password
     * @param winRatio Game win ratio
     * @param level User level
     * @param onlineStatus Whether user is online
     */
    public User(int userID, String username, String email, String password, double winRatio, int level, boolean onlineStatus) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.winRatio = winRatio;
        this.level = level;
        this.onlineStatus = onlineStatus;
    }

    // -------- Getters and Setters --------

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; save(); }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; save(); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; save(); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; save(); }

    public double getWinRatio() { return winRatio; }
    public void setWinRatio(double winRatio) { this.winRatio = winRatio; save(); }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; save(); }

    public boolean isOnline() { return onlineStatus; }
    public void setOnlineStatus(boolean onlineStatus) { this.onlineStatus = onlineStatus; save(); }

    /**
     * Saves the user to the database if auto-save is enabled
     */
    private void save() {
        if (!suspendSave) {
            UserDatabase.saveUser(this);
        }
    }

    /**
     * Controls whether auto-save should be temporarily disabled (used during batch updates)
     * @param suspendSave true to prevent auto-save, false to allow it
     */
    public void setSuspendSave(boolean suspendSave) {
        this.suspendSave = suspendSave;
    }
}
