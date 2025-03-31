package Authentication;

public class UpdateUserProfile {
    public boolean updateUser(int userId, String username, String email) {
        User user = UserDatabase.getUserById(userId);
        if (user == null) return false;

        if (username != null && !username.trim().isEmpty()) {
            user.setUsername(username);
        }
        if (email != null && email.contains("@")) {
            user.setEmail(email);
        }
        return true;
    }
}
