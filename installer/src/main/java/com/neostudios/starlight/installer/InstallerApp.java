// This file is part of the Maven project. If you see 'Missing mandatory Classpath entries' or 'non-project file', please reimport or refresh the Maven project in your IDE.
// The package declaration is correct for Maven: src/main/java/com/neostudios/starlight/installer/ -> package com.neostudios.starlight.installer;

package com.neostudios.starlight.installer;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the Starlight Game Installer (Swing version).
 * Guides the user through installation steps.
 */
public class InstallerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Starlight Installer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel root = new JPanel();
            frame.setContentPane(root);
            String os = System.getProperty("os.name").toLowerCase();
            InstallerController controller = new InstallerController(frame, root, os);
            // Use the controller instance for future extensibility or event wiring
            // For now, this ensures the instance is not ignored
            controller.toString(); // NOP usage to suppress warning
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            // Set window icon
            try {
                java.net.URL iconURL = InstallerApp.class.getResource("/assets/logo.png");
                if (iconURL != null) {
                    ImageIcon icon = new ImageIcon(iconURL);
                    frame.setIconImage(icon.getImage());
                }
            } catch (Exception e) {
                // Ignore if icon not found
            }
            if (!JavaDetector.isJavaAvailable()) {
                showJavaMissingDialog(frame, root);
            } 
            frame.setVisible(true);
        });
    }

    private static void showJavaMissingDialog(JFrame frame, JPanel root) {
        root.removeAll();
        JLabel label = new JLabel("Java is not installed or not found in PATH.\nThe Starlight Installer requires Java to run.");
        JButton download = new JButton("Download Java");
        JButton exit = new JButton("Exit");
        download.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new java.net.URI("https://adoptium.net/"));
            } catch (IOException | URISyntaxException ex) {
                label.setText("Could not open browser. Please visit adoptium.net manually.");
            }
        });
        exit.addActionListener(e -> frame.dispose());
        root.add(label);
        root.add(download);
        root.add(exit);
        root.revalidate();
        root.repaint();
    }
}
