package com.neostudios.starlight.installer;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * Utility for installing the game to a chosen location.
 */
public class InstallUtil {
    /**
     * Copies the game files to the install directory.
     * @param sourceDir The directory containing the cleaned game files.
     * @param installDir The target installation directory.
     * @throws IOException if copy fails
     */
    public static void copyGameFiles(File sourceDir, File installDir) throws IOException {
        Path src = sourceDir.toPath();
        Path dest = installDir.toPath();
        Files.walk(src).forEach(path -> {
            try {
                Path rel = src.relativize(path);
                Path target = dest.resolve(rel);
                if (Files.isDirectory(path)) {
                    Files.createDirectories(target);
                } else {
                    Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                // log or ignore
            }
        });
    }
}

// This file is part of the Maven project. If you see 'Missing mandatory Classpath entries' or 'non-project file', please reimport or refresh the Maven project in your IDE.
// The package declaration is correct for Maven: src/main/java/com/neostudios/starlight/installer/ -> package com.neostudios.starlight.installer;
