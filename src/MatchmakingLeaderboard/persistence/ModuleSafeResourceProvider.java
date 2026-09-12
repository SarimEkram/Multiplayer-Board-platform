package MatchmakingLeaderboard.persistence;

import org.flywaydb.core.api.ResourceProvider;
import org.flywaydb.core.api.resource.LoadableResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Supplies Flyway's migration SQL resources ourselves, instead of letting Flyway scan the
 * classpath for them.
 *
 * <p>When the application runs on the Java module path (e.g. {@code mvn javafx:run}, or a jlink
 * image), Flyway — an automatic module — cannot read resources that live in {@code
 * ca.ucalgary.groupprojectgui.p3}'s own module via its usual classpath scan, even with an {@code
 * opens} declaration, and fails with "Unable to obtain inputstream for resource". Loading the
 * resource via {@code MigrationRunner.class.getResourceAsStream(...)} instead always works,
 * because same-module resource access is never restricted.</p>
 */
class ModuleSafeResourceProvider implements ResourceProvider {

    private static final String LOCATION = "db/migration";

    @Override
    public LoadableResource getResource(String name) {
        String relativePath = name.startsWith(LOCATION + "/") ? name.substring(LOCATION.length() + 1) : name;
        // Flyway also probes for optional per-migration ".conf" files and expects null back when
        // one doesn't exist — must not unconditionally hand back a resource for any name.
        try (InputStream probe = ModuleSafeResourceProvider.class.getClassLoader()
                .getResourceAsStream(LOCATION + "/" + relativePath)) {
            if (probe == null) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        return new ClassResource(relativePath);
    }

    @Override
    public Collection<LoadableResource> getResources(String prefix, String[] suffixes) {
        List<LoadableResource> resources = new ArrayList<>();
        for (String filename : listMigrationFiles()) {
            if (filename.startsWith(prefix) && endsWithAny(filename, suffixes)) {
                resources.add(new ClassResource(filename));
            }
        }
        return resources;
    }

    private static boolean endsWithAny(String value, String[] suffixes) {
        for (String suffix : suffixes) {
            if (value.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lists the migration SQL files. Works by listing the {@code db/migration} directory when
     * it's a plain directory on disk (the case for `mvn test`/`mvn javafx:run` during
     * development); falls back to a maintained list for packaged (jar/jlink) runs, where
     * directory listing isn't available this way.
     */
    private static List<String> listMigrationFiles() {
        URL dirUrl = ModuleSafeResourceProvider.class.getClassLoader().getResource(LOCATION);
        if (dirUrl != null && "file".equals(dirUrl.getProtocol())) {
            try {
                File dir = new File(dirUrl.toURI());
                String[] names = dir.list((d, name) -> name.endsWith(".sql"));
                if (names != null) {
                    List<String> result = new ArrayList<>(List.of(names));
                    result.sort(String::compareTo);
                    return result;
                }
            } catch (Exception ignored) {
                // fall through to the hardcoded list below
            }
        }
        // Maintained fallback for packaged runs — update when adding a new migration file.
        return List.of("V1__init_schema.sql");
    }

    /** A LoadableResource backed by this module's own classloader (always readable). */
    private static class ClassResource extends LoadableResource {
        private final String relativePath;

        ClassResource(String relativePath) {
            this.relativePath = relativePath;
        }

        @Override
        public Reader read() {
            InputStream stream = ModuleSafeResourceProvider.class.getClassLoader()
                    .getResourceAsStream(LOCATION + "/" + relativePath);
            if (stream == null) {
                throw new IllegalStateException("Migration resource not found: " + LOCATION + "/" + relativePath);
            }
            return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        }

        @Override
        public String getFilename() {
            return relativePath;
        }

        @Override
        public String getRelativePath() {
            return LOCATION + "/" + relativePath;
        }

        @Override
        public String getAbsolutePath() {
            return "classpath:" + LOCATION + "/" + relativePath;
        }

        @Override
        public String getAbsolutePathOnDisk() {
            return getAbsolutePath();
        }
    }
}
