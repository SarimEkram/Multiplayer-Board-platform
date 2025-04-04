package Authentication;

public class UpdateUserProfile {
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
