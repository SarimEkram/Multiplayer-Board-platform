package Authentication;

import java.io.*;
import java.util.*;

public class UserDatabase {
    private static final String FILE_PATH = "userdata.csv";
    private static List<User> users = new ArrayList<>();

    static {
        loadUsersFromCSV();
    }

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
                user.setSuspendSave(true);
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

    public static boolean saveUser(User user) {
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID() == user.getUserID()) {
                users.set(i, user);
                found = true;
                break;
            }
        }
        if (!found) {
            user.setSuspendSave(true); // temporarily stop saving
            user.setUserID(getNextUserID());
            user.setSuspendSave(false); // re-enable saving
            users.add(user);
        }
        return saveAllToCSV();
    }

    public static boolean deleteUser(int userId) {
        users.removeIf(u -> u.getUserID() == userId);
        return saveAllToCSV();
    }

    public static User getUserById(int id) {
        return users.stream().filter(u -> u.getUserID() == id).findFirst().orElse(null);
    }

    public static User getUserByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }

    public static User getUserByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
    }

    private static int getNextUserID() {
        return users.stream().mapToInt(User::getUserID).max().orElse(0) + 1;
    }

    private static boolean saveAllToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            bw.write("userID,username,email,password,winRatio,level,onlineStatus\n");
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
}
