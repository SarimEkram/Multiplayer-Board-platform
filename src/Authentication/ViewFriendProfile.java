import java.util.ArrayList;
import java.util.List;

class Player {
    private String username;
    private String bio;
    private String location;
    private List<String> interests;
    private ArrayList<Player> friends;
    private boolean isInGame;

    public Player(String username, String bio, String location, List<String> interests) {
        this.username = username;
        this.bio = bio;
        this.location = location;
        this.interests = new ArrayList<>(interests);
        this.friends = new ArrayList<>();
        this.isInGame = false;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public List<String> getInterests() {
        return new ArrayList<>(interests);
    }

    public boolean isInGame() {
        return isInGame;
    }

    public void setInGame(boolean inGame) {
        this.isInGame = inGame;
    }

    public void addFriend(Player friend) {
        if (!friends.contains(friend)) {
            friends.add(friend);
            // Ensure mutual friendship
            friend.friends.add(this);
        }
    }

    public Player getFriend(String friendUsername) {
        for (Player friend : friends) {
            if (friend.getUsername().equals(friendUsername)) {
                return friend;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Username: " + username + "\nBio: " + bio + "\nLocation: " + location
                + "\nInterests: " + String.join(", ", interests);
    }
}

class ViewFriendsProfile {
    private ArrayList<Player> players;

    public ViewFriendsProfile() {
        players = new ArrayList<>();
    }

    public void registerPlayer(Player player) {
        players.add(player);
    }

    public boolean playerExists(String username) {
        for (Player player : players) {
            if (player.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public void viewFriendProfile(Player player, String friendUsername) {
        if (player.isInGame()) {
            System.out.println("Error: Cannot view profiles while in a game.");
            return;
        }

        if (!playerExists(friendUsername)) {
            System.out.println("Error: The user '" + friendUsername + "' does not exist in the database.");
            return;
        }

        Player friend = player.getFriend(friendUsername);
        if (friend != null) {
            System.out.println("Displaying profile for " + friend.getUsername() + ":\n" + friend);
        } else {
            System.out.println("Error: You are not connected to '" + friendUsername + "' as a friend.");
        }
    }
}
