package Authentication;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(123456, "TestUser", "test@example.com", "hashedPass", 0.75, 5, true);
        user.setSuspendSave(true);
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals(123456, user.getUserID());
        assertEquals("TestUser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashedPass", user.getPassword());
        assertEquals(0.75, user.getWinRatio());
        assertEquals(5, user.getLevel());
        assertTrue(user.isOnline());
    }

    @Test
    void testSettersUpdateValues() {
        user.setUserID(999999);
        user.setUsername("NewUser");
        user.setEmail("new@example.com");
        user.setPassword("newPass");
        user.setWinRatio(0.85);
        user.setLevel(10);
        user.setOnlineStatus(false);

        assertEquals(999999, user.getUserID());
        assertEquals("NewUser", user.getUsername());
        assertEquals("new@example.com", user.getEmail());
        assertEquals("newPass", user.getPassword());
        assertEquals(0.85, user.getWinRatio());
        assertEquals(10, user.getLevel());
        assertFalse(user.isOnline());
    }

    @Test
    void testSuspendSaveDisablesAutoSave() {
        user.setSuspendSave(true);
        user.setEmail("no-save@example.com");
        assertEquals("no-save@example.com", user.getEmail());
    }
}
