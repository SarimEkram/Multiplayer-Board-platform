package Authentication;

public class UserLogout {
    /**
     * Logs out a user from the system
     * @param userId The ID of user who is logged in
     * @return logout status
     */
    public static boolean logoutUser(int userId){
        // Check if userId is valid
        if (userId <= 0) {
            System.out.println("userID");
            return false;
        }
        // Remove user session or authentication token
        if (!endSession(userId)) {
            System.out.println("endSession");
            return false;
        }
        if (!clearAuthTokens(userId)) {
            System.out.println("clearAuthTokens");
            return false;
        }
        // If needed update session status in database
        User user = UserDatabase.getUserById(userId);

        user.setOnlineStatus(false);

        // if successful return true else false
        return (!(UserDatabase.getUserById(userId).isOnline()));
    }

    /**
     * Ends the user session
     * @param userId The ID of user who is logged in
     * @return status whether session is ended or not
     */
    private static boolean endSession(int userId){
        // Remove session data from system
        UserLogin.sessionData.remove(userId);

        return !UserLogin.sessionData.containsKey(userId); // check for successful removal
    }

    /**
     * Clear authentication tokens or cookies
     * @param userId The ID of user who is logged in
     * @return Status whether cookies/tokens cleared or not
     */
    private static boolean clearAuthTokens(int userId){
        // Delete authentication tokens
        UserLogin.authTokens.remove(userId);

        return !UserLogin.authTokens.containsKey(userId);
    }
}
