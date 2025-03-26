package Authentication;

public class User {

    private int userID;
    private String username;
    private String email;
    private String password; // Stored as a hashed password
    private double winRatio;
    private int level;
    private boolean onlineStatus;

    /**
     * Constructor to initialize a new user.
     *
     * @param userID Unique ID of the user
     * @param username User's chosen username
     * @param email User's email address
     * @param password User's hashed password
     * @param winRatio User's game win ratio
     * @param level User's level in the game
     * @param onlineStatus User's current online status
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

    // Getters and Setters

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        // Hash the password before storing
        this.password = hashPassword(password);
    }

    public double getWinRatio() {
        return winRatio;
    }

    public void setWinRatio(double winRatio) {
        // Win ratio should be between 0.0 and 1.0
        if (winRatio >= 0.0 && winRatio <= 1.0) {
            this.winRatio = winRatio;
        }
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        // Level should not be negative
        if (level >= 0) {
            this.level = level;
        }
    }

    public boolean isOnline() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean status) {
        this.onlineStatus = status;
    }

    // Methods for User Operations

    /**
     * Registers a new user.
     *
     * @return true if registration is successful, false otherwise
     */
    public boolean registerUser() {
        // Validate username, email, and password
        // Check if user already exists in database
        // Hash password before saving
        // Save user in database
        return false;
    }

    /**
     * Logs in the user.
     *
     * @param email The user's email
     * @param password The user's plain text password
     * @return true if login is successful, false otherwise
     */
    public boolean loginUser(String email, String password) {
        // Validate input
        // Check if user exists
        // Compare password with stored hashed password
        return false;
    }

    /**
     * Logs out the user.
     *
     * @return true if logout is successful, false otherwise
     */
    public boolean logoutUser() {
        // Clear session or authentication token
        // Set online status to false
        return false;
    }

    /**
     * Updates user account details.
     *
     * @param newUsername The new username (optional)
     * @param newEmail The new email (optional)
     * @param newPassword The new password (optional)
     * @return true if update successful, false otherwise
     */
    public boolean updateProfile(String newUsername, String newEmail, String newPassword) {
        // Validate new details
        // Hash password if updated
        // Save changes in database
        return false;
    }

    /**
     * Updates leaderboard stats.
     *
     * @param newWinRatio The updated win ratio
     * @param newLevel The updated level
     * @return true if update successful, false otherwise
     */
    public boolean updateLeaderboard(double newWinRatio, int newLevel) {
        // Validate new stats
        // Save changes in database
        return false;
    }

    /**
     * Views a friend's profile.
     *
     * @param friendId The ID of the friend
     * @return A User object containing friend's profile details, or null if not found
     */
    public User viewFriendProfile(int friendId) {
        // Check if friend exists
        // Fetch and return friend’s profile details
        return null;
    }

    /**
     * Checks a friend's online status.
     *
     * @param friendId The ID of the friend
     * @return true if friend is online, false otherwise
     */
    public boolean isFriendOnline(int friendId) {
        // Look in database or session store to see if friend is online
        return false;
    }

    /**
     * Retrieves the last seen timestamp of a friend.
     *
     * @param friendId The ID of the friend
     * @return The last seen timestamp as a String, or null if not found
     */
    public String getFriendLastSeen(int friendId) {
        // Fetch last seen timestamp from database
        return null;
    }

    /**
     * Sends a password reset request.
     *
     * @return true if request successful, false otherwise
     */
    public boolean resetPasswordRequest() {
        // Generate reset token
        // Send email with reset instructions
        return false;
    }

    /**
     * Resets the user's password.
     *
     * @param newPassword The new password entered by the user
     * @return true if password reset is successful, false otherwise
     */
    public boolean resetPassword(String newPassword) {
        // Validate token
        // Hash new password
        // Save in database
        return false;
    }

    /**
     * Deletes the user's account.
     *
     * @return true if account deletion is successful, false otherwise
     */
    public boolean deleteAccount() {
        // Remove user data from database
        return false;
    }

    /**
     * Hashes the password before saving.
     *
     * @param password Plain text password
     * @return Hashed password
     */
    private String hashPassword(String password) {
        // Hash password logic here
        return null;
    }
}
