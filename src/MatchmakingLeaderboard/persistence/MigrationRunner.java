package MatchmakingLeaderboard.persistence;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Applies the Flyway SQL migrations under {@code db/migration} to the given {@link DataSource}.
 * Called once, lazily, the first time {@link DataSourceProvider#getDataSource()} builds a pool —
 * covers both the embedded H2 test database and a real PostgreSQL instance.
 */
public class MigrationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationRunner.class);

    private MigrationRunner() {
    }

    public static void run(DataSource dataSource) {
        try {
            // Supplies migration resources ourselves via ModuleSafeResourceProvider rather than
            // letting Flyway scan the classpath for them: on the Java module path (e.g.
            // `mvn javafx:run`), Flyway (an automatic module) fails to read resources owned by
            // this module even when opened to it, because it's a foreign module doing the read.
            // Same-module resource access (which ModuleSafeResourceProvider uses) is never
            // restricted.
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .resourceProvider(new ModuleSafeResourceProvider())
                    .load();
            flyway.migrate();
        } catch (RuntimeException e) {
            LOG.error("Database migration failed", e);
            throw e;
        }
    }
}
