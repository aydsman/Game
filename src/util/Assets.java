package util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Resolves asset paths relative to the project root (directory containing {@code assets/}).
 * IntelliJ and other IDEs often run with a working directory that is not the project root.
 */
public final class Assets {

    private static final Path PROJECT_ROOT = findProjectRoot();

    private Assets() {}

  private static Path findProjectRoot() {
        Path cwd = Path.of("").toAbsolutePath().normalize();
        for (int i = 0; i < 4; i++) {
            if (Files.isDirectory(cwd.resolve("assets"))) {
                return cwd;
            }
            Path parent = cwd.getParent();
            if (parent == null) {
                break;
            }
            cwd = parent;
        }
        return Path.of("").toAbsolutePath().normalize();
    }

    public static Path getProjectRoot() {
        return PROJECT_ROOT;
    }

    /** Resolve a path that may start with {@code assets/} against the project root. */
    public static String resolve(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return relativePath;
        }
        Path path = Path.of(relativePath);
        if (path.isAbsolute()) {
            return relativePath;
        }
        return PROJECT_ROOT.resolve(relativePath).normalize().toString();
    }

    public static File file(String relativePath) {
        return new File(resolve(relativePath));
    }
}
