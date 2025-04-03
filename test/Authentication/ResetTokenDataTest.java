package Authentication;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ResetTokenData class.
 */
public class ResetTokenDataTest {

    @Test
    void testTokenCreationAndGetters() {
        int userId = 10101;
        String token = "abc123";
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(30);

        ResetTokenData resetToken = new ResetTokenData(userId, token, expiry);

        assertEquals(userId, resetToken.getUserId());
        assertEquals(token, resetToken.getToken());
        assertEquals(expiry, resetToken.getExpiry());
        assertFalse(resetToken.isExpired(), "Token should not be expired initially");
    }

    @Test
    void testIsExpiredWhenExpired() {
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(1);
        ResetTokenData expiredToken = new ResetTokenData(1001, "expiredToken", expiredTime);

        assertTrue(expiredToken.isExpired(), "Token should be marked as expired");
    }

    @Test
    void testIsExpiredWhenStillValid() {
        LocalDateTime futureTime = LocalDateTime.now().plusMinutes(10);
        ResetTokenData validToken = new ResetTokenData(1002, "validToken", futureTime);

        assertFalse(validToken.isExpired(), "Token should still be valid");
    }
}
