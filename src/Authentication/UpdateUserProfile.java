package Authentication;

/**
 * Provides functionality to update a user's profile, including username and email,
 * with checks to prevent duplicate usernames or emails in the system.
 */
public class UpdateUserProfile {
    /**
     * Updates the username and/or email of a user.
     *
     * @param userId   The ID of the user whose profile is to be updated.
     * @param username The new username (optional - can be null or blank).
     * @param email    The new email (optional - must contain "@" if provided).
     * @return true if the update is successful, false if the user doesn't exist
     *         or the new username/email is already taken by another user.
     */
    public boolean updateUser(int userId, String username, String email) {
        User user = UserDatabase.getUserById(userId);
        if (user == null) return false;

        // Check if new username is already taken (by another user)
        if (username != null && !username.trim().isEmpty()) {
            User existing = UserDatabase.getUserByUsername(username);
            if (existing != null && existing.getUserID() != user.getUserID()) {
                return false; // Username already taken by someone else
            }
            user.setUsername(username);
        }

        // Check if new email is already taken (by another user)
        if (email != null && email.contains("@")) {
            User existing = UserDatabase.getUserByEmail(email);
            if (existing != null && existing.getUserID() != user.getUserID()) {
                return false; // Email already taken by someone else
            }
            user.setEmail(email);
        }
        UserDatabase.saveUser(user);
        return true;
    }
}
