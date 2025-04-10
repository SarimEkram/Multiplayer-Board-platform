package Authentication;



public class ViewUserProfile {

    //This method retrieves and formats a user's profile details based on their user ID.
    public String getUserProfile(int userId) {

        // getting the user object from the UserDatabase using the given user ID
        User user = UserDatabase.getUserById(userId);


        // If the user does not exist, return a message indicating that "User not found."
        if (user == null) {
            return "User not found.";
        }


        // Creating a StringBuilder to build the profile output
        StringBuilder sb = new StringBuilder();

        // Appending the user profile information line by line
        sb.append("Your Profile:\n");
        sb.append("Username: ").append(user.getUsername()).append("\n");   // Username
        sb.append("Email: ").append(user.getEmail()).append("\n");         //Email
        sb.append("Level: ").append(user.getLevel()).append("\n");         //Level
        sb.append("Win Ratio: ").append(user.getWinRatio()).append("\n");  //Win or Lose Ratio

        // Appending the online status as "Yes" or "No" based on boolean value
        sb.append("Online: ").append(user.isOnline() ? "Yes" : "No");

        // Return the final string representation of the user's profile
        return sb.toString();
    }
}