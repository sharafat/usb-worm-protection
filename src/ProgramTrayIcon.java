import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Sharafat
 */
public class ProgramTrayIcon {
    private PopupMenu popup = new PopupMenu();
    private TrayIcon trayIcon;
    private SystemTray tray;
    private MainWindow mainWindow;
    private ScansManager scansManager;
    private DetectRemovableDevices detectRemDev;
    private PauseProtection pauseProtection;
    private boolean[] isProtectionEnabled = new boolean[1]; // Used array so that it can be passed
                                                            // as a reference to PauseProtection
    
    /**
     * @param mainWin Reference of the Main Window
     * @param scanManager Reference of the ScanManager object
     * @param detRemDev Reference of the DetectRemovableDevices objects
     * @param protectStatus Protection status: true = enabled, false = disabled
     */
    ProgramTrayIcon(MainWindow mainWin,ScansManager scanManager,
            DetectRemovableDevices detRemDev, boolean protectStatus) {
        mainWindow = mainWin;
        scansManager = scanManager;
        detectRemDev = detRemDev;
        isProtectionEnabled[0] = protectStatus;
        
        // Create popup menus
        MenuItem scanMyComputer = new MenuItem("Scan My Computer");
        popup.add(scanMyComputer);
        MenuItem scan = new MenuItem("Scan for Worms");
        popup.add(scan);
        popup.addSeparator();
        MenuItem openMainWindow = new MenuItem("Open USB Worm Protection");
        popup.add(openMainWindow);
        MenuItem preferences = new MenuItem("Preferences");
        popup.add(preferences);
        MenuItem about = new MenuItem("About");
        popup.add(about);
        popup.addSeparator();
        final MenuItem protectionStatus;
        if (isProtectionEnabled[0]) {
            protectionStatus = new MenuItem("Pause Protection");
        } else {
            protectionStatus = new MenuItem("Resume Protection");
        }        
        popup.add(protectionStatus);
        MenuItem exit = new MenuItem("Exit");
        popup.add(exit);
        
        //Create System Tray
        if (isProtectionEnabled[0]) {
            trayIcon = new TrayIcon(
                (new ImageIcon(Main.class.getResource("/images/icon.png"), "Tray Icon"))
                .getImage(), "USB Worm Protection", popup);
        } else {
            trayIcon = new TrayIcon(
                (new ImageIcon(Main.class.getResource(
                    "/images/disabledTrayIcon.png"), "Tray Icon (Disabled)")).getImage(),
                    "USB Worm Protection (Protection Disabled)", popup);
        }        
        tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(null, 
                    "Cannot display tray icon. Possible reason:\n"
                    + e.getMessage() + "\n\nProgram will now exit.", 
                    "USB Worm Protection", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
                
        // Detect screen size to display dialogs in the center of the window
        final Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Add action listeners
        scanMyComputer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the local hard disk drive letters
                File[] allDrives = File.listRoots();
                ArrayList<File> drives = new ArrayList<File>(1);
                for (File drive : allDrives) {
                    if (FileSystemView.getFileSystemView()
                            .getSystemTypeDescription(drive).equalsIgnoreCase("Local Disk")) {
                        drives.add(drive);
                    }
                }
                
                // Call ScansManager to start scanning
                drives.trimToSize();
                File[] drivesList = new File[drives.size()];
                scansManager.performScan(drives.toArray(drivesList));
            }
        });
        
        scan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainWindow.scanLabelClicked();
                mainWindow.setLocation(((screenDim.width - mainWindow.getWidth()) / 2),
                        ((screenDim.height - mainWindow.getHeight()) / 2));
                mainWindow.setVisible(true);
                mainWindow.setExtendedState(JFrame.NORMAL);
                mainWindow.toFront();
            }
        });
        
        openMainWindow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainWindow.scanLabelClicked();
                mainWindow.setLocation(((screenDim.width - mainWindow.getWidth()) / 2),
                        ((screenDim.height - mainWindow.getHeight()) / 2));                
                mainWindow.setVisible(true);
                mainWindow.setExtendedState(JFrame.NORMAL);
                mainWindow.toFront();
            }
        });
        
        preferences.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainWindow.preferencesLabelClicked();                
                mainWindow.setLocation(((screenDim.width - mainWindow.getWidth()) / 2),
                        ((screenDim.height - mainWindow.getHeight()) / 2));
                mainWindow.setVisible(true);
                mainWindow.setExtendedState(JFrame.NORMAL);
                mainWindow.toFront();
            }
        });
        
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainWindow.aboutLabelClicked();                
                mainWindow.setLocation(((screenDim.width - mainWindow.getWidth()) / 2),
                        ((screenDim.height - mainWindow.getHeight()) / 2));
                mainWindow.setVisible(true);
                mainWindow.setExtendedState(JFrame.NORMAL);
                mainWindow.toFront();
            }
        });
        
        protectionStatus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                if (isProtectionEnabled[0]) {
                    // Display Pause Protection dialog
                    Object[] param = {isProtectionEnabled, protectionStatus,
                                     detectRemDev, scansManager};
                    pauseProtection = new PauseProtection(null, false, param);
                    pauseProtection.setLocation(
                            ((screenDim.width - pauseProtection.getWidth()) / 2),
                            ((screenDim.height - pauseProtection.getHeight()) / 2));
                    pauseProtection.setVisible(true);                    
                } else {
                    if ((pauseProtection != null) && (pauseProtection.timer != null)) {
                        pauseProtection.timer.stop();
                    }
                    isProtectionEnabled[0] = true;
                    protectionStatus.setLabel("Disable Protection");
                    trayIcon.setImage(new ImageIcon(Main.class.getResource("/images/icon.png"), 
                            "Tray Icon").getImage());
                    trayIcon.setToolTip("USB Worm Protection");
                    detectRemDev = new DetectRemovableDevices(scansManager);
                    //detectRemDev.enableProtection();
                    
                    // Write preferences into file
                    Preferences preferences = new Preferences();    
                    File prefFile = new File("preferences.dat");                
                    boolean prefFileExists = prefFile.exists();
                    if (prefFileExists) {        
                        ObjectInputStream obin = null;
                        try {
                            obin = new ObjectInputStream(new FileInputStream("preferences.dat"));
                            preferences = (Preferences) obin.readObject();            
                        } catch (Exception e1) {
                        } finally {
                            try {
                                obin.close();
                            } catch (Exception e2) {}
                        }
                    }
                    
                    preferences.protectionEnableStatus = 0;
                    
                    ObjectOutputStream obout = null;
                    try {
                        obout = new ObjectOutputStream(new FileOutputStream("preferences.dat"));
                        obout.writeObject(preferences);        
                    } catch (IOException e3) {
                        JOptionPane.showMessageDialog(null, "Error: Cannot save protection status:\n" + e,
                                "USB Worm Protection", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        try {
                            obout.close();
                        } catch (IOException e4) {}
                    }
                    
                }
            }
        });
        
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
    }
    
}
