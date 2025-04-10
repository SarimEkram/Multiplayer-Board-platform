package Authentication;

import java.time.LocalDateTime;
/**
 * Represents a temporary token used for secure operations
 * such as password resets or session authentication.
 */
public class ResetTokenData {
    private int userId;
    private String token;
    private LocalDateTime expiry;

    /**
     * Constructs a new ResetTokenData object.
     *
     * @param userId The ID of the user the token belongs to
     * @param token The unique token string
     * @param expiry The expiration time of the token
     */
    public ResetTokenData(int userId, String token, LocalDateTime expiry) {
        this.userId = userId;
        this.token = token;
        this.expiry = expiry;
    }

    /**
     * Gets the user ID associated with this token.
     *
     * @return The user's ID
     */
    public int getUserId() { return userId; }
    /**
     * Gets the token string.
     *
     * @return The token string
     */
    public String getToken() { return token; }
    /**
     * Gets the expiry time of this token.
     *
     * @return The expiration timestamp
     */
    public LocalDateTime getExpiry() { return expiry; }
    /**
     * Checks if the token has expired.
     *
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiry);
    }
}
