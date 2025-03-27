package Authentication;


public class ViewUserProfile {

    public String getUserProfile(int userId) {
        User user = UserDatabase.getUserById(userId);
        if (user == null) {
            return "User not found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Your Profile:\n");
        sb.append("Username: ").append(user.getUsername()).append("\n");
        sb.append("Email: ").append(user.getEmail()).append("\n");
        sb.append("Level: ").append(user.getLevel()).append("\n");
        sb.append("Win Ratio: ").append(user.getWinRatio()).append("\n");
        sb.append("Online: ").append(user.isOnline() ? "Yes" : "No");

        return sb.toString();
    }
}