package Authentication;

/**
 * This class provides simple utilities to check the online status
 * of a friend (another user) by accessing data from the UserDatabase.
 */
public class FriendStatus{

    /**
     * Checks whether a friend with the specific user ID is currently online.
     * @param friendId The UserID of friend
     * @return true if the friend is online, false if offline or if the friend does not exist
     */
    public boolean isFriendOnline(int friendId){
        User friend = UserDatabase.getUserById(friendId);
        return (friend != null && friend.isOnline());
    }

    /**
     * Provides a status message indicating whether
     * a friend is online or not
     * @param friendId The UserID of friend
     * @return A string message like "Friend is online", "Friend is not online", or "Friend not found"
     */
    public String getFriendStatus(int friendId){
        User friend = UserDatabase.getUserById(friendId);
        if(friend == null){
            return "Friend not found";
        }
        if(friend.isOnline()) {
            return "Friend is online";
        }
        else{
            return "Friend is not online";
        }
    }
}