package com.neostudios.starlight.installer;

import java.io.IOException;

/**
 * Utility to detect if Java is available on the system.
 */
// This file is part of the Maven project. If you see 'Missing mandatory Classpath entries' or 'non-project file', please reimport or refresh the Maven project in your IDE.
// The package declaration is correct for Maven: src/main/java/com/neostudios/starlight/installer/ -> package com.neostudios.starlight.installer;
public class JavaDetector {
    /**
     * Checks if Java is available in the system PATH.
     * @return true if Java is available, false otherwise
     */
    public static boolean isJavaAvailable() {
        try {
            Process process = new ProcessBuilder("java", "-version").redirectErrorStream(true).start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException ex) {
            return false;
        }
    }
}
