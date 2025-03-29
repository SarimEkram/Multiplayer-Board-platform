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

        User loginTest = new User(0, "login", "login@example.com", UserLogin.hashPassword("login123456"), 0.5, 2, false);
        UserDatabase.saveUser(loginTest);

        //correct password
        if (UserLogin.loginUser("login@example.com", "login123456")) {
            System.out.println("User logged in.");
        }
        //incorrect
        if (UserLogin.loginUser("login@example.com", "12323412341234")) {
            System.out.println("This should not print");
        }
        if (UserLogout.logoutUser(loginTest.getUserID())) {
            System.out.println("User logged out.");
        }
        System.out.println(loginTest.isOnline());

        System.out.println(UserDatabase.getUserById(loginTest.getUserID()));

        System.out.println(DeleteUserAccount.deleteAccount(loginTest.getUserID()));
    }

}
