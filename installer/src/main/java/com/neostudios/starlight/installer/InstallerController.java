// This file is part of the Maven project. If you see 'Missing mandatory Classpath entries' or 'non-project file', please reimport or refresh the Maven project in your IDE.
// The package declaration is correct for Maven: src/main/java/com/neostudios/starlight/installer/ -> package com.neostudios.starlight.installer;

package com.neostudios.starlight.installer;

import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;

import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Controller for the installer wizard steps.
 */
public class InstallerController {
    private final JFrame frame;
    private final JPanel root;
    private File tempCloneDir;
    private File installDir;
    private boolean createDesktopShortcut = false;
    private static final String REPO_URL = "https://github.com/your-org/Project-Starlight.git"; // TODO: set actual repo
    private static final String GAME_JAR = "game-0.0.1.alpha1.jar";
    private final String os;

    public InstallerController(JFrame frame, JPanel root, String os) {
        this.frame = frame;
        this.root = root;
        this.os = os;
        showWelcome();
    }

    private void showWelcome() {
        root.removeAll();
        JLabel welcome = new JLabel("Welcome to the Starlight Game Installer!");
        welcome.setFont(new Font(os.contains("win") ? "Segoe UI" : os.contains("mac") ? "San Francisco" : "Ubuntu", Font.BOLD, 18));
        // Add logo
        JLabel logo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/assets/logo.png"));
            logo.setIcon(new ImageIcon(icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            // ignore if logo not found
        }
        JButton next = new JButton("Start Installation");
        next.addActionListener(e -> showChooseInstallDir());
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.add(logo);
        root.add(welcome);
        root.add(next);
        root.revalidate();
        root.repaint();
    }

    private void showChooseInstallDir() {
        root.removeAll();
        JLabel label = new JLabel("Choose installation directory:");
        JButton choose = new JButton("Choose Directory");
        JLabel chosen = new JLabel();
        choose.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            chooser.setDialogTitle("Select Installation Directory");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = chooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File dir = chooser.getSelectedFile();
                if (dir != null) {
                    installDir = dir;
                    chosen.setText("Selected: " + dir.getAbsolutePath());
                    // Check for existing installation
                    if (isGameInstalled(installDir)) {
                        String version = getInstalledVersion(installDir);
                        showExistingInstallOptions(version);
                    }
                }
            }
        });
        JButton next = new JButton("Next");
        next.addActionListener(e -> showOptions());
        root.add(label);
        root.add(choose);
        root.add(chosen);
        root.add(next);
        root.revalidate();
        root.repaint();
    }

    private boolean isGameInstalled(File dir) {
        return new File(dir, "game-0.0.1.alpha1.jar").exists();
    }

    private String getInstalledVersion(File dir) {
        File propFile = new File(dir, "assets/game.properties");
        if (propFile.exists()) {
            try {
                Properties props = new Properties();
                props.load(Files.newInputStream(propFile.toPath()));
                return props.getProperty("version", "unknown");
            } catch (IOException ex) {
                // log or ignore
            }
        }
        return "unknown";
    }

    private void showExistingInstallOptions(String version) {
        root.removeAll();
        JLabel found = new JLabel("Starlight is already installed (version: " + version + ").");
        JButton update = new JButton("Update");
        JButton uninstall = new JButton("Uninstall");
        JButton cancel = new JButton("Cancel");
        update.addActionListener(e -> showOptions());
        uninstall.addActionListener(e -> uninstallGame());
        cancel.addActionListener(e -> showWelcome());
        root.add(found);
        root.add(update);
        root.add(uninstall);
        root.add(cancel);
        root.revalidate();
        root.repaint();
    }

    private void uninstallGame() {
        root.removeAll();
        JLabel label = new JLabel("Are you sure you want to uninstall Starlight?");
        JButton yes = new JButton("Yes, Uninstall");
        JButton no = new JButton("No");
        yes.addActionListener(e -> {
            try {
                CleanupUtil.cleanup(installDir.toPath());
                // Optionally remove shortcuts
                // ...
                root.removeAll();
                root.add(new JLabel("Uninstall complete."));
            } catch (IOException ex) {
                root.removeAll();
                root.add(new JLabel("Uninstall failed: " + ex.getMessage()));
            }
            root.revalidate();
            root.repaint();
        });
        no.addActionListener(e -> showWelcome());
        root.add(label);
        root.add(yes);
        root.add(no);
        root.revalidate();
        root.repaint();
    }

    private void showOptions() {
        root.removeAll();
        JCheckBox desktopShortcut = new JCheckBox(os.contains("win") ? "Create Desktop Shortcut and Start Menu entry" : os.contains("mac") ? "Create Desktop Shortcut and Dock icon" : "Create Desktop and Menu Shortcut");
        desktopShortcut.addActionListener(e -> createDesktopShortcut = desktopShortcut.isSelected());
        JButton next = new JButton(os.contains("win") ? "Install (Windows style)" : os.contains("mac") ? "Install (macOS style)" : "Install (Linux style)");
        next.addActionListener(e -> startInstall());
        root.add(new JLabel("Options:"));
        root.add(desktopShortcut);
        root.add(next);
        root.revalidate();
        root.repaint();
    }

    private void startInstall() {
        root.removeAll();
        JProgressBar progress = new JProgressBar();
        JLabel status = new JLabel("Cloning repository...");
        root.add(status);
        root.add(progress);
        root.revalidate();
        root.repaint();
        new Thread(() -> {
            try {
                tempCloneDir = Files.createTempDirectory("starlight_clone").toFile();
                GitCloneUtil.cloneRepo(REPO_URL, tempCloneDir);
                SwingUtilities.invokeLater(() -> status.setText("Cleaning up files..."));
                CleanupUtil.cleanup(tempCloneDir.toPath());
                SwingUtilities.invokeLater(() -> status.setText("Copying game files..."));
                InstallUtil.copyGameFiles(tempCloneDir, installDir);
                // Create launcher script for all platforms
                ShortcutUtil.createLauncherScript(installDir.toPath(), GAME_JAR);
                if (createDesktopShortcut) {
                    SwingUtilities.invokeLater(() -> status.setText("Creating desktop shortcut..."));
                    ShortcutUtil.createDesktopShortcut(installDir.toPath(), GAME_JAR);
                }
                SwingUtilities.invokeLater(this::showSuccess);
            } catch (GitAPIException | IOException ex) {
                SwingUtilities.invokeLater(() -> showError(ex.getMessage()));
            }
        }).start();
    }

    private void showSuccess() {
        root.removeAll();
        root.add(new JLabel("Installation complete!"));
        root.add(new JButton("Finish"));
        root.revalidate();
        root.repaint();
    }

    private void showError(String message) {
        root.removeAll();
        root.add(new JLabel("Error: " + message));
        root.add(new JButton("Exit"));
        root.revalidate();
        root.repaint();
    }
}
