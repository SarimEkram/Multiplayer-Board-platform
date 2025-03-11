package Authentication;

public class FriendStatus {

    /**
     * Gets the current status of a friend (online/offline).
     *
     * @param friendId The ID of the friend
     * @return true if the friend is online, false otherwise
     */
    public boolean isFriendOnline(int friendId) {
        // Check if friendId is valid
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
     * Retrieves the full friend status, including online status and last seen.
     *
     * @param friendId The ID of the friend
     * @return A String message containing the friend's status
     */
    public String getFriendStatus(int friendId) {
        // Fetch online status
        boolean online = isFriendOnline(friendId);

        // Fetch last seen timestamp
        String lastSeen = getFriendLastSeen(friendId);

        // Construct and return a status message
        if (online) {
            return "Friend is online";
        } else {
            if (lastSeen != null) {
                return "Friend last seen at: " + lastSeen;
            } else {
                return "Friend last seen at: Unknown";
            }
        }
    }
}