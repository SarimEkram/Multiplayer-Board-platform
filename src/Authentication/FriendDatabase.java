package Authentication;

import java.io.*;
import java.util.*;

/**
 * Manages a user's friend relationships using a CSV file.
 * Each line maps one userID to a comma-separated list of friendIDs.
 */
public class FriendDatabase {

    private static final String FILE_PATH = "friends.csv";
    private static final Map<Integer, Set<Integer>> friendsMap = new HashMap<>();

    static {
        loadFromCSV();
    }

    // Load all friendship data from CSV
    public static void loadFromCSV() {
        friendsMap.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(":");
                int userId = Integer.parseInt(parts[0].trim());
                Set<Integer> friendIds = new HashSet<>();

                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    for (String id : parts[1].split(",")) {
                        friendIds.add(Integer.parseInt(id.trim()));
                    }
                }

                friendsMap.put(userId, friendIds);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save the current friendship data to CSV
    private static boolean saveToCSV() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Map.Entry<Integer, Set<Integer>> entry : friendsMap.entrySet()) {
                int userId = entry.getKey();
                String friendList = String.join(",", entry.getValue().stream()
                        .map(String::valueOf).toList());
                bw.write(userId + ":" + friendList + "\n");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add a friend (mutual)
    public static boolean addFriend(int userId, int friendId) {
        if (userId == friendId) return false; // Cannot friend yourself

        friendsMap.putIfAbsent(userId, new HashSet<>());
        friendsMap.putIfAbsent(friendId, new HashSet<>());

        boolean added1 = friendsMap.get(userId).add(friendId);
        boolean added2 = friendsMap.get(friendId).add(userId);

        return saveToCSV() && (added1 || added2);
    }

    // Remove a friend (mutual)
    public static boolean removeFriend(int userId, int friendId) {
        boolean removed1 = friendsMap.containsKey(userId) && friendsMap.get(userId).remove(friendId);
        boolean removed2 = friendsMap.containsKey(friendId) && friendsMap.get(friendId).remove(userId);

        return saveToCSV() && (removed1 || removed2);
    }

    // Get all friends of a user
    public static Set<Integer> getFriends(int userId) {
        return friendsMap.getOrDefault(userId, new HashSet<>());
    }

    // Check if two users are friends
    public static boolean areFriends(int userId, int friendId) {
        return friendsMap.containsKey(userId) && friendsMap.get(userId).contains(friendId);
    }

    // Clear all data and delete CSV (useful for testing)
    public static boolean deleteCSVFile() {
        friendsMap.clear();
        File file = new File(FILE_PATH);
        return file.exists() && file.delete();
    }
}
