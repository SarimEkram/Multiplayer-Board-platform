package Authentication;

public class ViewFriendProfile {

    public String getFriendProfile(int friendId) {
        User friend = UserDatabase.getUserById(friendId);
        if (friend == null) {
            return "Friend not found.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Friend Profile:\n");
        sb.append("Username: ").append(friend.getUsername()).append("\n");
        sb.append("Email: ").append(friend.getEmail()).append("\n");
        sb.append("Level: ").append(friend.getLevel()).append("\n");
        sb.append("Win Ratio: ").append(friend.getWinRatio()).append("\n");
        sb.append("Online: ").append(friend.isOnline() ? "Yes" : "No");

        return sb.toString();
    }
}