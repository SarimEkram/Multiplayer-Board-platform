import java.util.ArrayList;

    class Player {
        private String username;
        private ArrayList<Player> friends;

        public Player(String username) {
            this.username = username;
            this.friends = new ArrayList<>();
        }

        public String getUsername() {
            return username;
        }

        public void addFriend(Player friend) {
            if (!friends.contains(friend)) {
                friends.add(friend);
            }


        public Player getFriend(String friendUsername) {
            for (Player friend : friends) {
                if (friend.getUsername().equals(friendUsername)) {
                    return friend;
                }
            }
            return null;
        }


