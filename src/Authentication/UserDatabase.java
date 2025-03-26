package Authentication;

import java.util.HashMap;
import java.util.Map;

public class UserDatabase {
    // Instead of directly storing User objects, we store field-by-field data.
    private static final Map<Integer, Map<String, Object>> userData = new HashMap<>();
    // For quick lookup by email, store email -> userID
    private static final Map<String, Integer> emailIndex = new HashMap<>();
    private static int nextId = 1;

    // Retrieve a full User object by ID, reconstructing it from the field map
    public static User getUserById(int id) {
        Map<String, Object> fields = userData.get(id);
        if (fields == null) return null;

        // Rebuild a User object
        User user = new User();
        user.setUserID(id); // Be careful not to loop infinitely with save calls
        // We'll do a direct set on the fields to avoid re-calling setUserID:
        userDataToUser(fields, user);
        return user;
    }

    // Retrieve by email -> find userID, then call getUserById
    public static User getUserByEmail(String email) {
        if (email == null) return null;
        Integer userId = emailIndex.get(email.toLowerCase());
        if (userId == null) return null;
        return getUserById(userId);
    }
    public static User getUserByUsername(String username) {
        for (Map<String, Object> fields : userData.values()) {
            String existingUsername = (String) fields.get("username");
            if (existingUsername != null && existingUsername.equals(username)) {
                int id = -1;
                for (Map.Entry<Integer, Map<String, Object>> entry : userData.entrySet()) {
                    if (entry.getValue() == fields) {
                        id = entry.getKey();
                        break;
                    }
                }
                return getUserById(id);
            }
        }
        return null;
    }


    // Save or update a user to the field map
    public static boolean saveUser(User user) {
        if (user == null) return false;
        // If user has no valid ID, assign a new one
        if (user.getUserID() <= 0) {
            // Temporarily disable the auto-persist from setUserID to avoid recursion
            int newId = nextId++;
            // We'll do a manual field set
            userIDFieldSet(user, newId);
        }

        // Convert user fields to the map
        Map<String, Object> fields = userToUserData(user);
        userData.put(user.getUserID(), fields);

        // Update email index
        emailIndex.put(user.getEmail().toLowerCase(), user.getUserID());
        return true;
    }

    // Remove the user from the system
    public static boolean deleteUser(int userId) {
        Map<String, Object> removed = userData.remove(userId);
        if (removed == null) return false;

        // Remove from emailIndex too
        String email = (String) removed.get("email");
        if (email != null) {
            emailIndex.remove(email.toLowerCase());
        }
        return true;
    }

    // Helper to copy user object -> map of fields
    private static Map<String, Object> userToUserData(User user) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("username", user.getUsername());
        fields.put("email", user.getEmail());
        fields.put("password", user.getPassword());
        fields.put("winRatio", user.getWinRatio());
        fields.put("level", user.getLevel());
        fields.put("onlineStatus", user.isOnline());
        return fields;
    }


    private static void userDataToUser(Map<String, Object> fields, User user) {
        // Avoid triggering save inside setters during reconstruction
        userIDFieldSet(user, user.getUserID());

        user.setUsername((String) fields.get("username"));
        user.setEmail((String) fields.get("email"));
        user.setPassword((String) fields.get("password"));
        user.setWinRatio((double) fields.get("winRatio"));
        user.setLevel((int) fields.get("level"));
        user.setOnlineStatus((boolean) fields.get("onlineStatus"));
    }

    // Bypasses the setter so we don’t trigger infinite recursion.
    private static void userIDFieldSet(User user, int newId) {
        userData.remove(user.getUserID()); // in case it was set previously
        user.setUserID(newId);
    }
}
