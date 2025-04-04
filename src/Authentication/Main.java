package Authentication;

import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        System.out.println("=== Friend System ===");

        do {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add Friend");
            System.out.println("2. Remove Friend");
            System.out.println("3. View Friend List");
            System.out.println("4. Check Friendship");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter your User ID: ");
                    int userId = scanner.nextInt();
                    System.out.print("Enter Friend's User ID to add: ");
                    int friendId = scanner.nextInt();
                    boolean added = FriendDatabase.addFriend(userId, friendId);
                    System.out.println(added ? "Friend added successfully!" : "Failed to add friend.");
                }
                case 2 -> {
                    System.out.print("Enter your User ID: ");
                    int userId = scanner.nextInt();
                    System.out.print("Enter Friend's User ID to remove: ");
                    int friendId = scanner.nextInt();
                    boolean removed = FriendDatabase.removeFriend(userId, friendId);
                    System.out.println(removed ? "Friend removed successfully!" : "Failed to remove friend.");
                }
                case 3 -> {
                    System.out.print("Enter your User ID: ");
                    int userId = scanner.nextInt();
                    Set<Integer> friends = FriendDatabase.getFriends(userId);
                    if (friends.isEmpty()) {
                        System.out.println("No friends found.");
                    } else {
                        System.out.println("Your friends: " + friends);
                    }
                }
                case 4 -> {
                    System.out.print("Enter your User ID: ");
                    int userId = scanner.nextInt();
                    System.out.print("Enter Friend's User ID to check: ");
                    int friendId = scanner.nextInt();
                    boolean areFriends = FriendDatabase.areFriends(userId, friendId);
                    System.out.println(areFriends ? "You are friends." : "You are not friends.");
                }
                case 5 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);

        scanner.close();
    }
}
