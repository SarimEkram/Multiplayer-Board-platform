package MatchmakingLeaderboard.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;

/**
 * Provides the single, pooled {@link DataSource} used by {@code UserDatabase},
 * {@code PlayerDatabase}, and {@code FriendDatabase}. When {@link DatabaseConfig} reports no
 * configured database, falls back to an in-memory H2 instance running in PostgreSQL-compatibility
 * mode, so existing tests keep passing with no external setup (research.md §3, FR-007).
 */
public class DataSourceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceProvider.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_BASE_DELAY_MS = 100L;

    private static volatile HikariDataSource dataSource;

    private DataSourceProvider() {
    }

    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            HikariDataSource candidate = buildDataSource();
            try {
                MigrationRunner.run(candidate);
            } catch (RuntimeException e) {
                candidate.close();
                throw e;
            }
            dataSource = candidate;
        }
        return dataSource;
    }

    /**
     * Test-only hook: discards the current pool/database so a fresh embedded instance is built on
     * next access. Not used by production code paths.
     */
    static synchronized void resetForTests() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }

    private static HikariDataSource buildDataSource() {
        HikariConfig config = new HikariConfig();

        if (DatabaseConfig.isConfigured()) {
            LOG.info("Configuring PostgreSQL connection pool");
            config.setJdbcUrl(DatabaseConfig.getUrl());
            config.setUsername(DatabaseConfig.getUser());
            config.setPassword(DatabaseConfig.getPassword());
            config.setDriverClassName("org.postgresql.Driver");
        } else {
            LOG.info("DATABASE_URL not set; using embedded in-memory H2 database (test/dev fallback)");
            config.setJdbcUrl("jdbc:h2:mem:p3;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE");
            config.setUsername("sa");
            config.setPassword("");
            config.setDriverClassName("org.h2.Driver");
        }

        config.setMaximumPoolSize(10);
        config.setConnectionTimeout(5000);
        return new HikariDataSource(config);
    }

    /**
     * Runs {@code work} with a connection, retrying up to {@value #MAX_RETRY_ATTEMPTS} times with
     * a short backoff if the database is temporarily unreachable, per FR-004. Throws
     * {@link DatabaseUnavailableException} (a clear, unchecked error) if all attempts fail.
     */
    public static <T> T withConnection(SqlFunction<T> work) {
        SQLException lastFailure = null;
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try (Connection connection = getDataSource().getConnection()) {
                return work.apply(connection);
            } catch (SQLTransientConnectionException e) {
                lastFailure = e;
                LOG.warn("Database connection attempt {}/{} failed: {}", attempt, MAX_RETRY_ATTEMPTS, e.getMessage());
                sleepBeforeRetry(attempt);
            } catch (SQLException e) {
                LOG.error("Database operation failed", e);
                throw new DatabaseUnavailableException("Database operation failed", e);
            }
        }
        LOG.error("Database unreachable after {} attempts", MAX_RETRY_ATTEMPTS, lastFailure);
        throw new DatabaseUnavailableException(
                "Database unreachable after " + MAX_RETRY_ATTEMPTS + " attempts", lastFailure);
    }

    /**
     * Like {@link #withConnection}, but runs {@code work} inside a single transaction
     * (auto-commit disabled, committed on success, rolled back on failure). Use this for any
     * write that touches more than one row/table, per FR-003.
     */
    public static <T> T withTransaction(SqlFunction<T> work) {
        return withConnection(connection -> {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                T result = work.apply(connection);
                connection.commit();
                return result;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        });
    }

    private static void sleepBeforeRetry(int attempt) {
        try {
            Thread.sleep(RETRY_BASE_DELAY_MS * (1L << (attempt - 1)));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    public interface SqlFunction<T> {
        T apply(Connection connection) throws SQLException;
    }

    /** Clear, unchecked error surfaced to callers after retries are exhausted (FR-004). */
    public static class DatabaseUnavailableException extends RuntimeException {
        public DatabaseUnavailableException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
