// This file is part of the Maven project. If you see 'Missing mandatory Classpath entries' or 'non-project file', please reimport or refresh the Maven project in your IDE.
// The package declaration is correct for Maven: src/main/java/com/neostudios/starlight/installer/ -> package com.neostudios.starlight.installer;

package com.neostudios.starlight.installer;

import java.io.File;

import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Utility for cloning the Starlight game repository.
 */
public class GitCloneUtil {
    /**
     * Clones the given repository URL to the specified directory.
     * @param repoUrl The Git repository URL.
     * @param destDir The destination directory.
     * @throws GitAPIException if cloning fails
     */
    public static void cloneRepo(String repoUrl, File destDir) throws GitAPIException {
        try {
            org.eclipse.jgit.api.Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(destDir)
                .call();
        } catch (GitAPIException ex) {
            throw ex;
        }
    }
}
