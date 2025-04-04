package Authentication;

import java.time.LocalDateTime;

public class ResetTokenData {
    private int userId;
    private String token;
    private LocalDateTime expiry;

    public ResetTokenData(int userId, String token, LocalDateTime expiry) {
        this.userId = userId;
        this.token = token;
        this.expiry = expiry;
    }

    public int getUserId() { return userId; }
    public String getToken() { return token; }
    public LocalDateTime getExpiry() { return expiry; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiry);
    }
}
