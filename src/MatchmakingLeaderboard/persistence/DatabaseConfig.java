package MatchmakingLeaderboard.persistence;

/**
 * Reads database connection configuration from the environment (or system properties, which take
 * precedence — useful for tests). When no DATABASE_URL is configured, DataSourceProvider falls
 * back to an embedded H2 instance so existing tests keep working with zero setup (research.md §3).
 */
public class DatabaseConfig {

    private DatabaseConfig() {
    }

    public static boolean isConfigured() {
        return getUrl() != null && !getUrl().isBlank();
    }

    public static String getUrl() {
        return valueOf("DATABASE_URL");
    }

    public static String getUser() {
        return valueOf("DATABASE_USER");
    }

    public static String getPassword() {
        return valueOf("DATABASE_PASSWORD");
    }

    private static String valueOf(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty;
        }
        return System.getenv(key);
    }
}
