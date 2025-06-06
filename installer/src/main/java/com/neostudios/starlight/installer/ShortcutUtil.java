// This file is part of the Maven project. If you see 'Missing mandatory Classpath entries' or 'non-project file', please reimport or refresh the Maven project in your IDE.
// The package declaration is correct for Maven: src/main/java/com/neostudios/starlight/installer/ -> package com.neostudios.starlight.installer;

package com.neostudios.starlight.installer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility for creating desktop and start menu shortcuts (Windows only).
 * For Linux, creates .desktop files. For macOS, provides instructions.
 */
public class ShortcutUtil {
    private static final String WINDOWS_SHORTCUT_SCRIPT = """
            @echo off
            java -jar "%~dp0%s"
            """;

    private static final String LINUX_SHORTCUT_SCRIPT = """
            #!/bin/bash
            DIR="$(dirname \"$0\")"
            java -jar "$DIR/%s"
            """;

    private static final String MAC_SHORTCUT_SCRIPT = """
            #!/bin/bash
            DIR="$(dirname \"$0\")"
            java -jar "$DIR/%s"
            """;

    private static final String DESKTOP_ENTRY = """
            [Desktop Entry]
            Type=Application
            Name=Starlight
            Exec=java -jar %s
            Icon=%s
            Terminal=false
            """;

    /**
     * Creates a desktop shortcut or launcher for the game on the user's platform.
     * @param installDir The directory where the game is installed.
     * @param gameJarName The name of the game JAR file.
     */
    public static void createDesktopShortcut(Path installDir, String gameJarName) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        if (os.contains("win")) {
            // Windows: create a .bat launcher on Desktop
            Path batFile = Path.of(userHome, "Desktop", "Starlight.bat");
            String content = WINDOWS_SHORTCUT_SCRIPT.formatted(installDir.resolve(gameJarName).toAbsolutePath());
            Files.writeString(batFile, content);
        } else if (os.contains("linux")) {
            Path desktopFile = Path.of(userHome, "Desktop", "Starlight.desktop");
            String iconPath = installDir.resolve("assets/logo.png").toAbsolutePath().toString();
            String content = DESKTOP_ENTRY.formatted(installDir.resolve(gameJarName), iconPath);
            Files.writeString(desktopFile, content);
            desktopFile.toFile().setExecutable(true);
        } else if (os.contains("mac")) {
            // macOS: create a .command launcher on Desktop
            Path commandFile = Path.of(userHome, "Desktop", "Starlight.command");
            String content = MAC_SHORTCUT_SCRIPT.formatted(installDir.resolve(gameJarName).toAbsolutePath());
            Files.writeString(commandFile, content);
            commandFile.toFile().setExecutable(true);
        }
    }

    /**
     * Creates a launcher script in the install directory for all platforms.
     */
    public static void createLauncherScript(Path installDir, String gameJarName) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            Path batFile = installDir.resolve("Starlight.bat");
            String content = WINDOWS_SHORTCUT_SCRIPT.formatted(installDir.resolve(gameJarName).toAbsolutePath());
            Files.writeString(batFile, content);
        } else {
            Path shFile = installDir.resolve("Starlight.sh");
            String content = LINUX_SHORTCUT_SCRIPT.formatted(installDir.resolve(gameJarName).toAbsolutePath());
            Files.writeString(shFile, content);
            shFile.toFile().setExecutable(true);
        }
    }
}
