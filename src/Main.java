import javax.swing.*;
import java.io.*;

/**
 *
 * @author Sharafat
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Set Windows Look-&-Feel
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {}
                
                // Construct the preferences panel
                PreferencesPanel prefPanel = new PreferencesPanel();
                
                // Load the default preference values
                boolean shouldDoSilentScan = true;
                int suspectedFileAction = 0;    // 0 = Prompt, 1 = Delete, 2 = Quarantine, 3 = Leave
                int protectionEnableStatus = 0; // 0 = Enabled or Enable at next program restart,
                                                // 1 = Enable at user request only
                boolean shouldShowSlideInScanDialog = true;
                boolean shouldRemoveSlideInDialogAfterScan = false;
                boolean shouldAlwaysDisplayScanDetails = false;
                
                // Change the default preference values if necessary
                File prefFile = new File("preferences.dat");                
                boolean prefFileExists = prefFile.exists();
                if (prefFileExists) {
                    // Read the preferences and set the values
                    Preferences preferences = new Preferences();
                    ObjectInputStream obin = null;
                    try {
                        obin = new ObjectInputStream(new FileInputStream("preferences.dat"));
                        preferences = (Preferences) obin.readObject();
                        
                        shouldDoSilentScan = preferences.shouldDoSilentScan;
                        suspectedFileAction = preferences.suspectedFileAction;
                        protectionEnableStatus = preferences.protectionEnableStatus;
                        shouldShowSlideInScanDialog = preferences.shouldShowSlideInScanDialog;
                        shouldRemoveSlideInDialogAfterScan = preferences.shouldRemoveSlideInDialogAfterScan;
                        shouldAlwaysDisplayScanDetails = preferences.shouldAlwaysDisplayScanDetails;
                    } catch (Exception e) {
                    } finally {
                        try {
                            obin.close();
                        } catch (Exception e) {}
                    }
                    
                    // Modify the preferences panel according to user's selected preferences
                    prefPanel.setPreferences(preferences);
                }
                
                // Start Scans Manager
                Object[] param = {shouldDoSilentScan, suspectedFileAction, shouldShowSlideInScanDialog,
                                  shouldRemoveSlideInDialogAfterScan, shouldAlwaysDisplayScanDetails};
                                    
                ScansManager scansManager = new ScansManager(param);
                
                // Construct the Scan and About panels                
                ScanPanel scanPanel = new ScanPanel(scansManager);             
                About aboutPanel = new About();
                                
                // Construct the main window
                MainWindow mainWindow = new MainWindow(aboutPanel, scanPanel, prefPanel);
                
                // Start detecting removable devices
                DetectRemovableDevices detectRemDev = new DetectRemovableDevices(scansManager);
                
                // Construct the system tray according to protection status                
                if (protectionEnableStatus == 1) {    // i.e. "Enable at user request only"
                    // Disable on-access protection
                    detectRemDev.disableProtection();
                    
                    // Construct tray icon with the disabled icon
                    new ProgramTrayIcon(mainWindow, scansManager, detectRemDev, false);
                    
                    // Display a slide-in message warning the user about the disabled protection
                    SlideInDialog protectDisabledMsg = new SlideInDialog();
                    protectDisabledMsg.bodyPanel.add(new JLabel(
                            "Protection against worms is disabled.",
                            new ImageIcon(getClass().getResource("/images/attention.png")),
                            SwingConstants.CENTER));
                    protectDisabledMsg.background.setIcon(new ImageIcon(getClass()
                            .getResource("/images/background_yellow.png")));
                    new SlideInNotification(protectDisabledMsg);
                } else {
                    // Construct tray icon with the enabled icon
                    new ProgramTrayIcon(mainWindow, scansManager, detectRemDev, true);                        
                }
                
            }
        });
    }
    
    /**
     * Method for checking another instance of the program. (Used by JavaExe)
     * @param args
     * @return true
     */
    public static boolean isOneInstance(String[] args) {
        return true;
    };
    
}
