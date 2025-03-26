package Authentication;

public class User {
    private int userID;
    private String username;
    private String email;
    private String password;
    private double winRatio;
    private int level;
    private boolean onlineStatus;

    public User(int userID, String username, String email, String password, double winRatio, int level, boolean onlineStatus) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.winRatio = winRatio;
        this.level = level;
        this.onlineStatus = onlineStatus;
    }

    public User() {}

    public int getUserID() { return userID; }
    public void setUserID(int userID) {
        this.userID = userID;
        UserDatabase.saveUser(this);
    }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username;
        UserDatabase.saveUser(this);
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = email;
        UserDatabase.saveUser(this);
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
        UserDatabase.saveUser(this);
    }

    public double getWinRatio() { return winRatio; }
    public void setWinRatio(double winRatio) {
        this.winRatio = winRatio;
        UserDatabase.saveUser(this);
    }

    public int getLevel() { return level; }
    public void setLevel(int level) {
        this.level = level;
        UserDatabase.saveUser(this);
    }

    public boolean isOnline() { return onlineStatus; }
    public void setOnlineStatus(boolean onlineStatus) {
        this.onlineStatus = onlineStatus;
        UserDatabase.saveUser(this);
    }
}
