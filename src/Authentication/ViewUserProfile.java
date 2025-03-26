package Authentication;

import java.util.List;

public class ViewUserProfile {
    private String username;
    private String currentGameStatus;
    private int userID;
    private List<GameHistory> recentGames;
    private UserStats userStats;
    private Record record;

    public ViewUserProfile(String username, String currentGameStatus, int userID,
                           List<GameHistory> recentGames, UserStats userStats, Record record) {
        this.username = username;
        this.currentGameStatus = currentGameStatus;
        this.userID = userID;
        this.recentGames = recentGames;
        this.userStats = userStats;
        this.record = record;
    }

    public void viewProfile() {
        if (username == null || username.isEmpty()) {
            System.out.println("Error: Invalid user data. Profile could not be loaded.");
            return;
        }

        System.out.println("Profile of " + username);

        if (currentGameStatus == null) {
            System.out.println("Error: Current game status is unavailable.");
        } else {
            System.out.println("Current Game Status: " + currentGameStatus);
        }

        System.out.println("User ID: " + userID);


        System.out.println("---Recent Games---");
        if (recentGames == null || recentGames.isEmpty()) {
            System.out.println("No recent games available.");
        } else {
            for (GameHistory game : recentGames) {
                System.out.println(game); // Assuming GameHistory has a meaningful toString() method
            }
        }

        System.out.println("---User Stats---");
        if (userStats == null) {
            System.out.println("User stats not available.");
        } else {
            System.out.println("Wins: " + userStats.getWins());
            System.out.println("Losses: " + userStats.getLosses());
            System.out.println("Total Games Played: " + userStats.getTotalGames());
        }

        System.out.println("---User Record---");
        if (record == null) {
            System.out.println("User record not available.");
        } else {
            System.out.println("Highest Score: " + record.getHighestScore());
            System.out.println("Longest Winning Streak: " + record.getLongestWinningStreak());
        }
    }

}