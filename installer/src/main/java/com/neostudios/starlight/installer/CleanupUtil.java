// This file is part of the Maven project. If you see 'Missing mandatory Classpath entries' or 'non-project file', please reimport or refresh the Maven project in your IDE.
// The package declaration is correct for Maven: src/main/java/com/neostudios/starlight/installer/ -> package com.neostudios.starlight.installer;

package com.neostudios.starlight.installer;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * Utility class for cleaning up non-game files after cloning the repo.
 */
public class CleanupUtil {
    private static final List<String> REMOVE_FILES = List.of(
            "README.md", "README_TEMPLATE.md", "CONTRIBUTING.md", "CODE_OF_CONDUCT.md", "SECURITY.md", "SUPPORT.md", "guide-to-java.md", "tree.md", ".gitignore", ".gitattributes", ".github", ".idea", "installer", "src", "test", "docs", "LICENSE"
    );

    /**
     * Deletes files and directories not needed for the game runtime.
     * @param rootDir The root directory of the cloned repo.
     * @throws IOException if deletion fails
     */
    public static void cleanup(Path rootDir) throws IOException {
        for (String name : REMOVE_FILES) {
            Path target = rootDir.resolve(name);
            if (Files.exists(target)) {
                if (Files.isDirectory(target)) {
                    deleteDirectory(target);
                } else {
                    Files.delete(target);
                }
            }
        }
    }

    private static void deleteDirectory(Path dir) throws IOException {
        Files.walk(dir)
                .sorted((a, b) -> b.compareTo(a)) // delete children first
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // log or ignore
                    }
                });
    }
}
