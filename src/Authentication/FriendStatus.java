package Authentication;

public class FriendStatus{
    public boolean isFriendOnline(int friendId){
        User friend = UserDatabase.getUserById(friendId);
        return (friend != null && friend.isOnline());
    }
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