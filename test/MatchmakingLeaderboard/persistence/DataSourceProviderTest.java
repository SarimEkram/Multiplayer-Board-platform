package MatchmakingLeaderboard.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceProviderTest {

    @AfterEach
    void resetSystemPropertiesAndPool() {
        System.clearProperty("DATABASE_URL");
        System.clearProperty("DATABASE_USER");
        System.clearProperty("DATABASE_PASSWORD");
        DataSourceProvider.resetForTests();
    }

    @Test
    void fallsBackToEmbeddedH2WhenNoDatabaseUrlConfigured() {
        assertFalse(DatabaseConfig.isConfigured(), "no DATABASE_URL should mean 'not configured'");

        try (Connection connection = DataSourceProvider.getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users")) {
            assertTrue(resultSet.next());
        } catch (Exception e) {
            fail("Embedded H2 fallback should be usable with no external setup: " + e.getMessage());
        }
    }

    @Test
    void reportsConfiguredWhenDatabaseUrlSystemPropertyIsSet() {
        System.setProperty("DATABASE_URL", "jdbc:postgresql://localhost:5432/p3");
        System.setProperty("DATABASE_USER", "p3");
        System.setProperty("DATABASE_PASSWORD", "p3");

        assertTrue(DatabaseConfig.isConfigured());
        assertEquals("jdbc:postgresql://localhost:5432/p3", DatabaseConfig.getUrl());
    }
}
