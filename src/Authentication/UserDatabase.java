package Authentication;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The UserDatabase class handles storing, loading, and managing users from a CSV file called "userdata.csv"
 *  This class uses an in-memory list of User objects and keeps it in sync with the CSV file.
 */

public class UserDatabase {
    private static final String FILE_PATH = "userdata.csv";     // Path to the CSV file
    private static List<User> users = new ArrayList<>();

    // Static block: loads users from CSV when class is first accessed
    static {
        loadUsersFromCSV();
    }

    /**
     * Loads all user records from the CSV file into memory
     */
    public static void loadUsersFromCSV() {
        users.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("userID")) continue;
                String[] parts = line.split(",");
                if (parts.length != 7) continue;

                User user = new User();
                user.setSuspendSave(true);    // Temporarily disable auto-save to avoid recursion
                user.setUserID(Integer.parseInt(parts[0]));
                user.setUsername(parts[1]);
                user.setEmail(parts[2]);
                user.setPassword(parts[3]);
                user.setWinRatio(Double.parseDouble(parts[4]));
                user.setLevel(Integer.parseInt(parts[5]));
                user.setOnlineStatus(Boolean.parseBoolean(parts[6]));
                user.setSuspendSave(false);

                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves or updates a user in the database.
     * If the user already exists (by ID), they are updated.
     * If new, a new ID is assigned, they are added to the list
     * @param user The user to save
     * @return true if save is successful
     */
    public static boolean saveUser(User user) {
        boolean found = false;
        // Check if user already exists by ID and update if so
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID() == user.getUserID()) {
                users.set(i, user);
                found = true;
                break;
            }
        }

        // If not found, assign new ID and add to list
        if (!found) {
            user.setSuspendSave(true); // temporarily stop saving
            user.setUserID(generateUniqueUserID());
            user.setSuspendSave(false); // re-enable saving
            users.add(user);
        }
        return saveAllToCSV();     // Save all users to file
    }

    /**
     * Deletes a user by their ID.
     * @param userId The ID of the user to delete
     * @return true if user was deleted
     */
    public static boolean deleteUser(int userId) {
        users.removeIf(u -> u.getUserID() == userId);
        return saveAllToCSV();
    }

    /**
     * Gets a user by their unique ID.
     * @param id user ID
     * @return User object or null if not found
     */
    public static User getUserById(int id) {
        return users.stream().filter(u -> u.getUserID() == id).findFirst().orElse(null);
    }

    /**
     * Gets a user by their email address.
     * @param email The user's email
     * @return User object or null if not found
     */

    public static User getUserByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }

    /**
     * Gets a user by their username.
     * @param username user's username
     * @return User object or null if not found
     */
    public static User getUserByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    public static int generateUniqueUserID(){
        int userID;
        do{
            userID = ThreadLocalRandom.current().nextInt(100000, 999999);
        }
        while (getUserById(userID)!=null);
        return userID;
    }

    /**
     * Writes all users in memory back to the CSV file.
     * @return true if file write was successful
     */
    private static boolean saveAllToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // Write header
            bw.write("userID,username,email,password,winRatio,level,onlineStatus\n");
            // Write each user as CSV row
            for (User u : users) {
                bw.write(u.getUserID() + "," +
                        u.getUsername() + "," +
                        u.getEmail() + "," +
                        u.getPassword() + "," +
                        u.getWinRatio() + "," +
                        u.getLevel() + "," +
                        u.isOnline() + "\n");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes the entire CSV file and clears in-memory user data.
     * @return true if file was deleted successfully
     */
    public static boolean deleteCSVFile() {
        users.clear();  // Clear the in-memory user list
        File file = new File(FILE_PATH);

        System.out.println("Attempting to delete file: " + file.getAbsolutePath());
        if (file.exists()) {
            boolean deleted = file.delete();
            System.out.println("CSV deleted: " + deleted);
            return deleted;
        } else {
            System.out.println("CSV file does not exist.");
            return false;
        }
    }
}
