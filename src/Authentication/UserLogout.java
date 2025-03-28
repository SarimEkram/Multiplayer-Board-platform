package Authentication;

public class UserLogout {
    /**
     * Logs out a user from the system
     * @param userId The ID of user who is logged in
     * @return logout status
     */
    public boolean logoutUser(int userId){
        // Check if userId is valid
        if (userID <= 0) {
            return false;
        }
        // Remove user session or authentication token
        if (!endSession(userId)) {
            return false;
        }
        if (!clearAuthTokens(userId)) {
            return false;
        }
        // If needed update session status in database
        new UserStatus().updateUserStatus(userID, false);

        // if successful return true else false
        return (!UserStatus.IsUserOnline(userId));
    }

    /**
     * Ends the user session
     * @param userId The ID of user who is logged in
     * @return status whether session is ended or not
     */
    private boolean endSession(int userId){
        // Remove session data from system
        UserLogin.sessionData.remove(userId);

        if (UserLogin.sessiondata.contains(userId)) {
            return false; // check for successful removal
        }

        return true;
    }

    /**
     * Clear authentication tokens or cookies
     * @param userId The ID of user who is logged in
     * @return Status whether cookies/tokens cleared or not
     */
    private boolean clearAuthTokens(int userId){
        // Delete authentication tokens
        UserLogin.authTokens.remove(userID);

        if (UserLogin.authTokens.remove(userId)) {
            return false;
        }

        return true;
    }
}
