package Authentication;

public class User {
    private int userID;
    private String username;
    private String email;
    private String password;
    private double winRatio;
    private int level;
    private boolean onlineStatus;
    private boolean suspendSave = false;

    public User() {}

    public User(int userID, String username, String email, String password, double winRatio, int level, boolean onlineStatus) {
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.winRatio = winRatio;
        this.level = level;
        this.onlineStatus = onlineStatus;
    }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; save(); }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; save(); }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; save(); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; save(); }

    public double getWinRatio() { return winRatio; }
    public void setWinRatio(double winRatio) { this.winRatio = winRatio; save(); }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; save(); }

    public boolean isOnline() { return onlineStatus; }
    public void setOnlineStatus(boolean onlineStatus) { this.onlineStatus = onlineStatus; save(); }

    private void save() {
        if (!suspendSave) {
            UserDatabase.saveUser(this);
        }
    }

    public void setSuspendSave(boolean suspendSave) {
        this.suspendSave = suspendSave;
    }
}
