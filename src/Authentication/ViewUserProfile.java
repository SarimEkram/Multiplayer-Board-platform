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
        System.out.println("Profile of " + username);
        System.out.println("Current Game Status: " + currentGameStatus);
        System.out.println("User ID: " + userID);
        System.out.println("---Recent Games---");
