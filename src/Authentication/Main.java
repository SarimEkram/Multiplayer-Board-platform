package Authentication;

public class Main {
    public static void main(String[] args) {
        // Create and save a test user
        User testUser = new User(0, "tester", "test@example.com", "hashedpass123", 0.5, 2, false);
        UserDatabase.saveUser(testUser);

        // Read the same user back from the CSV using email
        User loadedUser = UserDatabase.getUserByEmail("test@example.com");

        // Print to verify
        if (loadedUser != null) {
            System.out.println("User read from CSV:");
            System.out.println("ID: " + loadedUser.getUserID());
            System.out.println("Username: " + loadedUser.getUsername());
            System.out.println("Email: " + loadedUser.getEmail());
            System.out.println("Level: " + loadedUser.getLevel());
            System.out.println("Win Ratio: " + loadedUser.getWinRatio());
        } else {
            System.out.println("User not found in CSV.");
        }
    }
}
