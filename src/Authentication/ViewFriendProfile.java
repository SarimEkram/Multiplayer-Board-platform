package Authentication;

/**
 * This class is responsible for retrieving and displaying a friend's profile information.
 */
public class ViewFriendProfile {

    /**
     * Retrieves and formats a friend's profile information as a readable string.
     *
     * @param friendId The unique user ID of the friend whose profile is being retrieved.
     * @return A formatted string containing the friend's profile details, or
     *         "Friend not found." if no such user exists in the database.
     */
    public String getFriendProfile(int friendId) {
        // Fetch user from database using the provided ID
        User friend = UserDatabase.getUserById(friendId);
        if (friend == null) {
            return "Friend not found.";
        }

        // Build formatted profile string with friend's details
        StringBuilder sb = new StringBuilder();
        sb.append("Friend Profile:\n");
        sb.append("Username: ").append(friend.getUsername()).append("\n");  // Friend's username
        sb.append("Email: ").append(friend.getEmail()).append("\n");        // Friend's email address
        sb.append("Level: ").append(friend.getLevel()).append("\n");        // Friend's current level
        sb.append("Win Ratio: ").append(friend.getWinRatio()).append("\n"); // Friend's win/loss ratio

        // Appending the online status as "Yes" or "No" based on boolean value
        sb.append("Online: ").append(friend.isOnline() ? "Yes" : "No");


        // Return the final string representation of the user's profile
        return sb.toString();
    }
}