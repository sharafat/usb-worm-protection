/*
 * Scan.java
 *
 * Created on July 24, 2008, 10:35 AM
 */

import java.awt.event.ActionEvent;
import java.io.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.*;
import javax.swing.border.*;

/**
 *
 * @author  Sharafat
 */
public class Scan extends javax.swing.JPanel {
    private ScanFrame scanFrame;
    protected DoScan doScan;
    
    // Preferences (set from the constructor parameters)
    private boolean shouldDoSilentScan;
    private int suspectedFileAction;    // 0 = Prompt, 1 = Delete,
                                        // 2 = Quarantine, 3 = Leave
    private boolean shouldShowSlideInScanDialog;
    private boolean shouldRemoveSlideInDialogAfterScan;
    private boolean shouldAlwaysDisplayScanDetails;
    
    private Scan previousScan;
    private Scan nextScan;
    
    // Stores scan duration for the timer
    private Timer timer;
    private int hour, min, sec;
    
    // Checks for pause/stop scanning command
    protected boolean shouldStopScanning = false;
    protected boolean shouldPauseScanning = false;

    /** Creates new single-drive scanning service (only for plugged-in USB mass storage devices)
     * @param drive String containing the Drive letter of the drive to be filesScanned
     * @param scanFrame The Scan dialog in which this Scan Panel is contained
     * @param preferences The preferences put together in a single array of type Object
     * @param previousScan 
     */
    public Scan(File drive, ScanFrame scanFrame, Object[] preferences,
                Scan previousScan) {
        this.scanFrame = scanFrame;
        shouldDoSilentScan = (Boolean) preferences[0];
        suspectedFileAction = (Integer) preferences[1];
        shouldShowSlideInScanDialog = (Boolean) preferences[2];
        shouldRemoveSlideInDialogAfterScan = (Boolean) preferences[3];
        shouldAlwaysDisplayScanDetails = (Boolean) preferences[4];
        this.previousScan = previousScan;
        initComponents();
        initialGUIUpdates();
        doScan = new DoScan(drive, this);
        doScan.execute();
    }
    
    /** Creates new multiple-drives scanning service (drives selected through scan panel)
     * @param drives Array of type File containing the drives to be filesScanned
     * @param scanFrame The Scan dialog in which this Scan Panel is contained
     * @param preferences The preferences put together in a single array of type Object
     * @param previousScan 
     */
    public Scan(File[] drives, ScanFrame scanFrame, Object[] preferences,
                Scan previousScan) {
        this.scanFrame = scanFrame;
        shouldDoSilentScan = false;
        suspectedFileAction = (Integer) preferences[1];
        shouldShowSlideInScanDialog = (Boolean) preferences[2];
        shouldRemoveSlideInDialogAfterScan = (Boolean) preferences[3];
        shouldAlwaysDisplayScanDetails = true;
        this.previousScan = previousScan;
        initComponents();
        initialGUIUpdates();
        doScan = new DoScan(drives);
        doScan.execute();
    }
    
    private void initialGUIUpdates() {        
        // Enable Previous Scan button
        if (previousScan != null) {
            prev.setEnabled(true);
        }
        
        // Add to scan frame
        scanFrame.panel.removeAll();
        scanFrame.validate();
        scanFrame.panel.repaint();
        scanFrame.panel.add(this);
        
        // Display scanning details
        if (shouldAlwaysDisplayScanDetails) {
            Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
            scanFrame.setLocation(((screenDim.width - scanFrame.getWidth()) / 2),
                        ((screenDim.height - scanFrame.getHeight()) / 2));
            scanFrame.setVisible(true);
        }
        
        // Set scan start time
        startTime.setText(DateFormat.getDateTimeInstance().format(new Date()));
        
        // Start timer to calculate duration
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((++sec) >= 60) {
                    if ((++min) >= 60) {
                        hour++;
                        hourLbl.setText(formatTime(hour));
                        min = 0;
                        minLbl.setText("00");
                    } else {
                        minLbl.setText(formatTime(min));
                    }
                    sec = 0;
                    secLbl.setText("00");
                } else {
                    secLbl.setText(formatTime(sec));
                }
            }
            
            private String formatTime(int time) {
                if (time < 10) {
                    return "0" + time;
                } else {
                    return Integer.toString(time);
                }                
            }
        });
        timer.start();
    }
          
    protected void setNextScan(Scan nextScan) {
        this.nextScan = nextScan;
        next.setEnabled(true);
    }
        
    class DoScan extends SwingWorker<Void, Void> {
        // Array of Drives to be scanned
        private File[] root;
        
        // Fields for updating the progress bar
        private long driveSize = 0;
        private long scannedFilesSize;
        private SlideInScanPanel slideInScanPanel;
        private SlideInDialog slideInDialog;
        
        // Fields for updating the GUI
        private ImageIcon scan_endImage = new ImageIcon(getClass().getResource("/images/scan_end.png"));        
        private ImageIcon scanImage = new ImageIcon(getClass().getResource("/images/scan.gif"));        
        protected boolean isCurrentlyShowing = true;
        private String title = "Scanning ";
        private String titleWithPercentage;
        private int totalFilesScanned = 0;
        private int totalFilesDetected = 0;
        private int totalFilesSuspected = 0;
        private int totalFilesDeleted = 0;
        private int totalFilesQuarantined = 0;
        private int totalFilesSkipped = 0;
        private int localFilesScanned = 0;
        private int localFilesDetected = 0;
        private int localFilesSuspected = 0;
        private int localFilesDeleted = 0;
        private int localFilesQuarantined = 0;
        private int localFilesSkipped = 0;
        
        // Fields for updating the tables
        protected DefaultTableModel detectedTableModel = (DefaultTableModel) detectedTable.getModel();
        protected DefaultTableModel suspectedTableModel = (DefaultTableModel) suspectedTable.getModel();
        private DefaultTableModel statTableModel = (DefaultTableModel) statTable.getModel();
        private JLabel fileStatus;
        private int currentStatTableDriveRowNo = 1; // To update Stat Table statistics
        
        // Fields for differentiating between detection and suspicion
        private ArrayList<File> detectedFiles = new ArrayList<File>(2);
        private ArrayList<File> suspectedFiles = new ArrayList<File>(5);        
        
        /**
         * Constructor for scanning removable media
         * @param drive The drive to be filesScanned
         */
        DoScan(File drive, Scan scan) {
            root = new File[1];
            root[0] = drive;
            
            // Get total drive space
            driveSize = drive.getTotalSpace() - drive.getFreeSpace();
            
            // Show slide-in scanning dialog
            if (shouldShowSlideInScanDialog) {
                slideInScanPanel = new SlideInScanPanel(scan, this, scanFrame);
                slideInScanPanel.scanning.setText("Scanning " + FileSystemView
                        .getFileSystemView().getSystemDisplayName(drive) + "...");
                slideInDialog = new SlideInDialog();
                slideInDialog.bodyPanel.add(slideInScanPanel);
                new SlideInNotification(slideInDialog);
            }
            
            // Add rows to Statistics table
            statTable.getColumnModel().getColumn(0).setCellRenderer(new StatTableCellRenderer());
            JLabel allLabel = new JLabel("All",
                    new javax.swing.ImageIcon(getClass().getResource("/images/info2.png")),
                    SwingConstants.LEADING);
            statTableModel.addRow(new Object[]{allLabel, 0, 0, 0, 0, 0, 0});
            JLabel driveLabel = new JLabel(FileSystemView.getFileSystemView()
                    .getSystemDisplayName(drive),
                    FileSystemView.getFileSystemView().getSystemIcon(drive),
                    SwingConstants.LEADING);
            statTableModel.addRow(new Object[]{driveLabel, 0, 0, 0, 0, 0, 0});
        }
        
        /**
         * Constructor for scanning drives selected from the main window
         * @param drives The array of drives to be filesScanned
         */
        DoScan(File[] drives) {
            root = drives;
            
            // Get total file size for all the drives
            for (File drive : drives) {
                driveSize += (drive.getTotalSpace() - drive.getFreeSpace());
            }
            
            // Add rows to Statistics table
            statTable.getColumnModel().getColumn(0).setCellRenderer(new StatTableCellRenderer());
            JLabel allLabel = new JLabel("All",
                    new javax.swing.ImageIcon(getClass().getResource("/images/info2.png")),
                    SwingConstants.LEADING);
            statTableModel.addRow(new Object[]{allLabel, 0, 0, 0, 0, 0, 0});
            for (File drive : drives) {
                JLabel driveLabel = new JLabel(FileSystemView.getFileSystemView()
                        .getSystemDisplayName(drive),
                    FileSystemView.getFileSystemView().getSystemIcon(drive),
                    SwingConstants.LEADING);
                statTableModel.addRow(new Object[]{driveLabel, 0, 0, 0, 0, 0, 0});
            }
        }
        
        @Override
        public Void doInBackground() {
            // Set Scan Frame title            
            for (File drive : root) {
                title += FileSystemView.getFileSystemView()
                        .getSystemDisplayName(drive) + ", ";
            }
            title = title.substring(0, title.length() - 2);
            titleWithPercentage = title;
            scanFrame.setTitle(title);                        
            
            // Start scanning
            for (File drive : root) {
                scan(drive);

                currentStatTableDriveRowNo++;
                
                localFilesScanned = 0;
                localFilesDetected = 0;
                localFilesSuspected = 0;
                localFilesDeleted = 0;
                localFilesQuarantined = 0;
                localFilesSkipped = 0;
            }            

            // Update GUI with messages indicating scanning finished
            if (!isCancelled()) {
                nowScanning.setText("Finished");
                location.setText("");
                progress.setValue(100);
                titleWithPercentage = "(100%) " + title;
                if (isCurrentlyShowing) {
                    scanFrame.setTitle(titleWithPercentage);                
                }
                finishTime.setText(DateFormat.getDateTimeInstance().format(new Date()));
                timer.stop();
                if (slideInScanPanel != null) {
                    slideInScanPanel.scanning.setText("Finished scanning " + FileSystemView.getFileSystemView().getSystemDisplayName(root[0]) + ".");
                    slideInScanPanel.progress.setValue(100);
                    
                    slideInScanPanel.pause.removeMouseListener(slideInScanPanel.pause.getMouseListeners()[0]);
                    slideInScanPanel.pause.removeMouseListener(slideInScanPanel.pause.getMouseListeners()[0]);
                    slideInScanPanel.pause.setForeground(Color.DARK_GRAY);
                    slideInScanPanel.pause.setText("<html>Pause</html>");
                    slideInScanPanel.pause.setCursor(Cursor.getDefaultCursor());
                    slideInScanPanel.stop.removeMouseListener(slideInScanPanel.stop.getMouseListeners()[0]);
                    slideInScanPanel.stop.removeMouseListener(slideInScanPanel.stop.getMouseListeners()[0]);
                    slideInScanPanel.stop.setForeground(Color.DARK_GRAY);
                    slideInScanPanel.stop.setText("<html>Stop</html>");
                    slideInScanPanel.stop.setCursor(Cursor.getDefaultCursor());
                }
                pause.setEnabled(false);
                stop.setEnabled(false);
            }
            
            // Change the animating image to still image
            img.setIcon(scan_endImage);
            
            // Remove Slide-in dialog
            if (shouldRemoveSlideInDialogAfterScan) {
                slideInDialog.removeDialog();
            }
            
            // Decrease number of currently running scans
            ScansManager.noOfRunningScans--;
            ScanPanel.scanningsLbl.setText("There are currently "
                + ScansManager.noOfRunningScans + " scan(s) in progress.");            
            
            // Enable Action... buttons and display tips in Detected & Suspected tables
            if (detectedTableModel.getRowCount() > 0) {
                detectedAction.setEnabled(true);
                detectedMsg.setIcon(new ImageIcon(getClass().getResource("/images/tip.png")));
            }
            if (suspectedTableModel.getRowCount() > 0) {
                suspectedAction.setEnabled(true);
                suspectedMsg.setIcon(new ImageIcon(getClass().getResource("/images/tip.png")));
            }
            
            // Show lists of detected or suspected files if exist
            if ((detectedTableModel.getRowCount() > 0) && !shouldDoSilentScan) {
                tabs.setSelectedIndex(0);
                if (!scanFrame.isVisible()) {
                    Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
                    scanFrame.setLocation(((screenDim.width - scanFrame.getWidth()) / 2),
                                ((screenDim.height - scanFrame.getHeight()) / 2));
                    scanFrame.setVisible(true);
                }
            } else if ((suspectedTableModel.getRowCount() > 0) && (suspectedFileAction == 0)) {
                tabs.setSelectedIndex(1);
                if (!scanFrame.isVisible()) {
                    Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
                    scanFrame.setLocation(((screenDim.width - scanFrame.getWidth()) / 2),
                                ((screenDim.height - scanFrame.getHeight()) / 2));
                    scanFrame.setVisible(true);
                }
            }
            
            return null;
        }

        protected synchronized void resume() {            
            shouldPauseScanning = false;
            pause.setText("Pause");
            img.setIcon(scanImage);
            stop.setEnabled(true);
            notify();
        }
        
        private void scan(File root) {
            File[] folders = null;
            File[] files;
            try {
                files = root.listFiles(new FileFilter() {
                                      public boolean accept(File file) {
                                          if (file.isFile()) {
                                              return true;
                                          } else {
                                              return false;
                                          }                                              
                                      }                                          
                                  });

                folders = root.listFiles(new FileFilter() {
                                     public boolean accept(File file) {
                                         if (file.isDirectory()) {
                                             return true;
                                         } else {
                                             return false;
                                         }                                         
                                     }                                     
                                 });

                // Check for pausing scan
                synchronized (this) {
                    while (shouldPauseScanning) {
                        try {
                            pause.setEnabled(true);
                            img.setIcon(scan_endImage);
                            wait();
                        } catch (InterruptedException e) {}
                    }
                }                 
                
                // Search for worm files
                if (files != null) {
                    for (File currFile : files) {
                        // Check for pausing scan
                        synchronized (this) {
                            while (shouldPauseScanning) {
                                try {
                                    pause.setEnabled(true);
                                    img.setIcon(scan_endImage);
                                    wait();
                                } catch (InterruptedException e) {}
                            }
                        }
                        
                        if (!shouldStopScanning) {
                            // Update GUI
                            updateUIBeforeFileScan(currFile);
                            
                            long fileLength = currFile.length();                            
                            String ext = currFile.getName().substring(currFile.getName().length() - 3);

                            // Check for hidden exe, cmd, bat, vb, vbs, vbe, vbscript, js, jse, ws, wsf, wsh, pif,
                            //                  scr, sct, script files
                            if (currFile.isHidden() && (ext.equalsIgnoreCase("exe") || ext.equalsIgnoreCase("cmd")
                                    || ext.equalsIgnoreCase("bat") || ext.equalsIgnoreCase("vb")
                                    || ext.equalsIgnoreCase("vbs") || ext.equalsIgnoreCase("vbe")
                                    || ext.equalsIgnoreCase("vbscript")
                                    || ext.equalsIgnoreCase("js")  || ext.equalsIgnoreCase("jse")
                                    || ext.equalsIgnoreCase("ws")  || ext.equalsIgnoreCase("wsf")
                                    || ext.equalsIgnoreCase("wsh") || ext.equalsIgnoreCase("pif")
                                    || ext.equalsIgnoreCase("scr") || ext.equalsIgnoreCase("sct")
                                    || ext.equalsIgnoreCase("script"))) {
                                // Worm file - take action                    
                                takeDetectedAction(currFile);
                            } // Check for hidden com files
                            else if (currFile.isHidden() && (ext.equalsIgnoreCase("com"))) {
                                // Check whether this is the NTDETECT.COM system file
                                if (currFile.getName().equalsIgnoreCase("NTDETECT.COM")) {
                                    boolean isSystemFile = false;
                                    for (File sys : files) {
                                        if (sys.getName().substring(sys.getName().length() - 3).equalsIgnoreCase("sys")) {
                                            isSystemFile = true;
                                            break;
                                        }
                                    }
                                    if (!isSystemFile) {
                                        // Worm file - take action
                                        takeDetectedAction(currFile);
                                    }
                                } else {
                                    // Worm file - take action
                                    takeDetectedAction(currFile);
                                }
                            } // Check whether this is New Folder.exe worm file
                            else if (currFile.getName().equalsIgnoreCase("New Folder.exe")
                                        || currFile.getName().equalsIgnoreCase("Flashy.exe")) {
                                // Worm file - take action
                                takeDetectedAction(currFile);
                            } // Check whether this is a Brontok worm file
                            else if (currFile.getName().toUpperCase().contains("DATA") && ext.equalsIgnoreCase("exe")) {
                                // Worm file - take action
                                takeDetectedAction(currFile);
                            } // Check whether this is the autorun.inf file
                            else if (currFile.getName().equalsIgnoreCase("autorun.inf")) {
                                // autorun.inf file - take action                                
                                takeDetectedAction(currFile);
                            }

                            // Update GUI
                            updateUIAfterFileScan(fileLength);
                        } else {
                            cancel(true);
                            updateUIForScanAbortion();
                            return;
                        }
                    }
                }

                // Check for pausing scan
                synchronized (this) {
                    while (shouldPauseScanning) {
                        try {
                            pause.setEnabled(true);
                            img.setIcon(scan_endImage);
                            wait();
                        } catch (InterruptedException e) {}
                    }
                }
                                 
                // Check the folders for the existence of a file name identical to the
                // name of the folder with .exe extension
                if (folders != null) {
                    for (File currFolder : folders) {
                        for (File file : files) {
                            // Check for pausing scan
                            synchronized (this) {
                                while (shouldPauseScanning) {
                                    try {
                                        pause.setEnabled(true);
                                        img.setIcon(scan_endImage);
                                        wait();
                                    } catch (InterruptedException e) {}
                                }
                            }
                            
                            if (!shouldStopScanning) {
                                if (file.getName().contains(currFolder.getName() + ".exe")) {
                                    // Worm file - take action
                                    takeDetectedAction(file);

                                    // Unhide the folder if it is hidden
                                    if (currFolder.isHidden()) {
                                        try {                                            
                                            String cmdLine = new String("attrib -H \"" 
                                                    + currFolder.getCanonicalPath() + "\"");                                            
                                            Runtime.getRuntime().exec(cmdLine);
                                        } catch (Throwable t) {
                                        }
                                    }
                                    break;
                                }
                            } else {
                                cancel(true);
                                updateUIForScanAbortion();
                                return;
                            }
                        }
                    }
                }
            } catch (Exception e) {}
            
            // Now start scanning the drive entirely, i.e. inside the current folders
            if (folders != null) {
                for (File folder : folders) {
                    if (!shouldStopScanning) {
                        scanFiles(folder);
                    } else {
                        updateUIForScanAbortion();
                        return;
                    }
                }
            }
        }
        
        private void scanFiles(File folder) {
            File[] folders = null;
            File[] files;
            try {
                files = folder.listFiles(new FileFilter() {
                            public boolean accept(File file) {
                                try {
                                    Thread.sleep(10);
                                } catch (Exception e) {}
                                if ((file.getName().length() > 3) &&
                                        file.getName().substring(file.getName()
                                            .length() - 3).equalsIgnoreCase("exe")) {                                                    
                                    return true;
                                } else {
                                    if (file.isFile()) {
                                        try {
                                            updateUIBeforeFileScan(file);
                                        } catch (IOException ex) {
                                        }
                                        updateUIAfterFileScan(file.length());
                                    }
                                    return false;
                                }                                
                            }                                            
                        });
                        
                folders = folder.listFiles(new FileFilter() {
                           public boolean accept(File file) {
                               if (file.isDirectory()) {                                               
                                   return true;
                               } else {
                                   return false;
                               }
                           }                                       
                       });

                // Check for pausing scan
                synchronized (this) {
                    while (shouldPauseScanning) {
                        try {
                            pause.setEnabled(true);
                            img.setIcon(scan_endImage);
                            wait();
                        } catch (InterruptedException e) {}
                    }
                }
                       
                // Search for worm files
                if (files != null) {                    
                    for (File file : files) {
                        // Check for pausing scan
                        synchronized (this) {
                            while (shouldPauseScanning) {
                                try {
                                    pause.setEnabled(true);
                                    img.setIcon(scan_endImage);
                                    wait();
                                } catch (InterruptedException e) {}
                            }
                        }
                        
                        if (!shouldStopScanning) {
                            updateUIBeforeFileScan(file);
                            long fileLength = file.length();
                            if (file.getName().contains(file.getParentFile().getName()) &&
                                    file.getName().length() <=
                                    (file.getParentFile().getName().length() + 5)) {
                                // Suspected worm file - take action
                                if (isUndoubtedlyWorm(file)) {
                                    takeDetectedAction(file);
                                } else {
                                    takeSuspectedAction(file);
                                }
                            }                            
                            updateUIAfterFileScan(fileLength);
                        } else {
                            cancel(true);
                            updateUIForScanAbortion();
                            return;                            
                        }
                    }
                }
            } catch (Exception e) {}
            
            // Check for pausing scan
            synchronized (this) {
                while (shouldPauseScanning) {
                    try {
                        pause.setEnabled(true);
                        img.setIcon(scan_endImage);
                        wait();
                    } catch (InterruptedException e) {}
                }
            }
            
            // Start scanning inside the children folders
            if (folders != null) {
                for (File currFolder : folders) {
                    // Check for pausing scan
                    synchronized (this) {
                        while (shouldPauseScanning) {
                            try {
                                pause.setEnabled(true);
                                img.setIcon(scan_endImage);
                                wait();
                            } catch (InterruptedException e) {}
                        }
                    }
                        
                    if (!shouldStopScanning) {
                        scanFiles(currFolder);
                    } else {
                        cancel(true);
                        updateUIForScanAbortion();
                        return;
                    }
                }
            }
        }
        
        private void updateUIBeforeFileScan(File currFile) throws IOException {
            nowScanning.setText(currFile.getName());
            location.setText(currFile.getCanonicalPath());
        }
        
        private void updateUIAfterFileScan(long fileLength) {
            scannedFilesSize += fileLength;
            int completed = (int) (((float) scannedFilesSize / (float) driveSize) * 100);
            progress.setValue(completed);
            titleWithPercentage = "(" + completed + "%) " + title;
            if (isCurrentlyShowing) {
                scanFrame.setTitle(titleWithPercentage);                
            }
            scanned.setText(Integer.toString(++totalFilesScanned));
            statTableModel.setValueAt(totalFilesScanned, 0, 1);
            statTableModel.setValueAt(++localFilesScanned, currentStatTableDriveRowNo, 1);
            
            // Update progress bar in slide-in dialog
            if (slideInScanPanel != null) {
                slideInScanPanel.progress.setValue(completed);
            }
        }

        private void updateUIForScanAbortion() {
            nowScanning.setText("Aborted");
            location.setText("");          
            finishTime.setText(DateFormat.getDateTimeInstance().format(new Date()));
            timer.stop();            
            if (slideInScanPanel != null) {               
                try {                
                    slideInScanPanel.scanning.setText("Aborted scanning " + FileSystemView
                                .getFileSystemView().getSystemDisplayName(root[0]) + ".");
                } catch (Exception e) {
                    slideInScanPanel.scanning.setText("Scanning Aborted.");
                }
            }            
        }        
        
        private boolean isUndoubtedlyWorm(File currFile) throws IOException {            
            for (File confirmedSize : detectedFiles) {
                if (currFile.length() == confirmedSize.length()) {
                    return true;
                }
            }
            
            int count = 0;
            for (File suspected : suspectedFiles) {
                if (currFile.length() == suspected.length()) {
                    count++;
                }
            }
            if (count >= 2) {
                // Clean-up suspected files for the current length
                try {
                    for (int i = 0; i < suspectedFiles.size(); i++) {
                        if (currFile.length() == suspectedFiles.get(i).length()) {
                            takeDetectedAction(suspectedFiles.get(i));
                            suspected.setText(Integer.toString(--totalFilesSuspected));
                            suspectedTableModel.removeRow(i);
                            suspectedFiles.remove(i);
                            statTableModel.setValueAt(totalFilesSuspected, 0, 3);
                            statTableModel.setValueAt(--localFilesSuspected, currentStatTableDriveRowNo, 3);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {}                
                return true;
            } else {
                return false;
            }
        }
        
        private void takeDetectedAction(File currFile) throws IOException {
            detectedFiles.add(currFile);
            JLabel file = new JLabel(currFile.getCanonicalPath(),
                    FileSystemView.getFileSystemView().getSystemIcon(currFile),
                    SwingConstants.LEADING);
            if (shouldDoSilentScan) {                        
                try {                            
                    currFile.delete();
                    fileStatus = new JLabel("Deleted",
                        new javax.swing.ImageIcon(getClass().getResource("/images/deleted.png")),
                        SwingConstants.LEADING);
                    statTableModel.setValueAt(++totalFilesDeleted, 0, 4);
                    statTableModel.setValueAt(++localFilesDeleted, currentStatTableDriveRowNo, 4);
                } catch (Exception e) {
                    fileStatus = new JLabel("Cannot Delete",
                        new javax.swing.ImageIcon(getClass().getResource("/images/detected.png")),
                        SwingConstants.LEADING);
                    fileStatus.setToolTipText(e.getMessage());
                    detectedMsg.setText("<html><font color = 'red'>Some errors occurred. For details, hover "
                        + "mouse on the 'Cannot Delete' messages and pause for a while.</font></html>");
                    detectedMsg.setIcon(null);
                    statTableModel.setValueAt(++totalFilesSkipped, 0, 4);
                    statTableModel.setValueAt(++localFilesSkipped, currentStatTableDriveRowNo, 6);
                }
            } else {
                fileStatus = new JLabel("Detected",
                    new javax.swing.ImageIcon(getClass().getResource("/images/detected.png")),
                            SwingConstants.LEADING);
            }
            detectedTableModel.addRow(new Object[]{fileStatus, file});
            detected.setText(Integer.toString(++totalFilesDetected));
            statTableModel.setValueAt(totalFilesDetected, 0, 2);
            statTableModel.setValueAt(++localFilesDetected, currentStatTableDriveRowNo, 2);
        }
        
        private void takeSuspectedAction(File currFile) throws IOException {
            suspectedFiles.add(currFile);
            JLabel file = new JLabel(currFile.getCanonicalPath(),
                    FileSystemView.getFileSystemView().getSystemIcon(currFile),
                    SwingConstants.LEADING);
            switch(suspectedFileAction) {
                case 0: // Prompt for action
                    fileStatus = new JLabel("Suspected",
                    new javax.swing.ImageIcon(getClass().getResource("/images/suspected.png")),
                            SwingConstants.LEADING);
                    break;
                case 1: // Delete
                    try {                            
                        currFile.delete();
                        fileStatus = new JLabel("Deleted",
                            new javax.swing.ImageIcon(getClass().getResource("/images/deleted.png")),
                            SwingConstants.LEADING);
                        statTableModel.setValueAt(++totalFilesDeleted, 0, 2);
                        statTableModel.setValueAt(++localFilesDeleted, currentStatTableDriveRowNo, 2);
                    } catch (Exception e) {
                        fileStatus = new JLabel("Cannot Delete",
                            new javax.swing.ImageIcon(getClass().getResource("/images/detected.png")),
                            SwingConstants.LEADING);
                        fileStatus.setToolTipText(e.getMessage());
                        detectedMsg.setText("<html><font color = 'red'>Some errors occurred. For details, hover "
                            + "mouse on the 'Cannot Delete' messages and pause for a while.</font></html>");
                        detectedMsg.setIcon(null);
                        statTableModel.setValueAt(++totalFilesSkipped, 0, 6);
                        statTableModel.setValueAt(++localFilesSkipped, currentStatTableDriveRowNo, 6);
                    }
                    break;
                case 2: // Quarantine
                    BufferedInputStream bin = null;
                    BufferedOutputStream bout = null;
                    try {
                        bin = new BufferedInputStream(new FileInputStream(currFile));
                        bout = new BufferedOutputStream(new FileOutputStream("Quarantine/" 
                                + currFile.getName().substring(0, currFile.getName().length() - 4) 
                                + "~" + System.currentTimeMillis() 
                                + currFile.getName().substring(currFile.getName().length() - 4)));                        
                        while (bin.available() > 0) {
                            bout.write(bin.read());
                        }
                        bin.close();
                        currFile.delete();
                        fileStatus = new JLabel("Quarantined",
                            new javax.swing.ImageIcon(getClass().getResource("/images/deleted.png")),
                            SwingConstants.LEADING);
                        statTableModel.setValueAt(++totalFilesQuarantined, 0, 5);
                        statTableModel.setValueAt(++localFilesQuarantined, currentStatTableDriveRowNo, 5);
                    } catch (Exception e) {
                        fileStatus = new JLabel("Cannot Quarantine",
                            new javax.swing.ImageIcon(getClass().getResource("/images/detected.png")),
                            SwingConstants.LEADING);
                        fileStatus.setToolTipText(e.getMessage());
                        detectedMsg.setText("<html><font color = 'red'>Some errors occurred. For details, hover "
                            + "mouse on the 'Cannot Delete' messages and pause for a while.</font></html>");
                        detectedMsg.setIcon(null);
                        statTableModel.setValueAt(++totalFilesSkipped, 0, 6);
                        statTableModel.setValueAt(++localFilesSkipped, currentStatTableDriveRowNo, 6);
                    } finally {
                        try {                            
                            bout.close();
                            bin.close();
                        } catch (Exception e) {}
                    }
                    break;
                case 3: // Leave
                    fileStatus = new JLabel("Skipped",
                            new javax.swing.ImageIcon(getClass().getResource("/images/deleted.png")),
                            SwingConstants.LEADING);
                    statTableModel.setValueAt(++totalFilesSkipped, 0, 6);
                    statTableModel.setValueAt(++localFilesSkipped, currentStatTableDriveRowNo, 6);
            }
            suspectedTableModel.addRow(new Object[]{fileStatus, file});
            suspected.setText(Integer.toString(++totalFilesSuspected));
            statTableModel.setValueAt(totalFilesSuspected, 0, 3);
            statTableModel.setValueAt(++localFilesSuspected, currentStatTableDriveRowNo, 3);
        }
        
    }
    
    class JLabelRenderer extends DefaultTableCellRenderer {        
        @Override
        public Component getTableCellRendererComponent(
                        JTable table, Object value,
                        boolean isSelected, boolean hasFocus,
                        int row, int column) {        
            Color fg = null;
            Color bg = null;

            JTable.DropLocation dropLocation = table.getDropLocation();
            if (dropLocation != null
                    && !dropLocation.isInsertRow()
                    && !dropLocation.isInsertColumn()
                    && dropLocation.getRow() == row
                    && dropLocation.getColumn() == column) {

                fg = UIManager.getColor("Table.dropCellForeground");
                bg = UIManager.getColor("Table.dropCellBackground");

                isSelected = true;
            }

            if (isSelected) {
                super.setForeground(fg == null ? table.getSelectionForeground()
                                               : fg);
                super.setBackground(bg == null ? table.getSelectionBackground()
                                               : bg);
            } else {
                super.setForeground(table.getForeground());
                super.setBackground(table.getBackground());
            }

            setFont(table.getFont());

            if (hasFocus) {
                Border border = null;
                if (isSelected) {
                    border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
                }
                if (border == null) {
                    border = UIManager.getBorder("Table.focusCellHighlightBorder");
                }
                setBorder(border);

                if (!isSelected && table.isCellEditable(row, column)) {
                    Color col;
                    col = UIManager.getColor("Table.focusCellForeground");
                    if (col != null) {
                        super.setForeground(col);
                    }
                    col = UIManager.getColor("Table.focusCellBackground");
                    if (col != null) {
                        super.setBackground(col);
                    }
                }
            } else {
                if (System.getSecurityManager() != null) {
                    setBorder(new EmptyBorder(1, 1, 1, 1));
                } else {
                    setBorder(new EmptyBorder(1, 1, 1, 1));
                }
            }
            
            JLabel label = (JLabel) value;
            setText(label.getText());
            setIcon(label.getIcon());            
            if ((column == 0) && (label.getToolTipText() != null)) {
                setToolTipText(label.getToolTipText());
            }
            if (column == 1) {
                setToolTipText(label.getText());
            }
            return this;
        }
    }
    
    class StatTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                        JTable table, Object value,
                        boolean isSelected, boolean hasFocus,
                        int row, int column) {        
            Color fg = null;
            Color bg = null;

            JTable.DropLocation dropLocation = table.getDropLocation();
            if (dropLocation != null
                    && !dropLocation.isInsertRow()
                    && !dropLocation.isInsertColumn()
                    && dropLocation.getRow() == row
                    && dropLocation.getColumn() == column) {

                fg = UIManager.getColor("Table.dropCellForeground");
                bg = UIManager.getColor("Table.dropCellBackground");

                isSelected = true;
            }

            if (isSelected) {
                super.setForeground(fg == null ? table.getSelectionForeground()
                                               : fg);
                super.setBackground(bg == null ? table.getSelectionBackground()
                                               : bg);
            } else {
                super.setForeground(table.getForeground());
                super.setBackground(table.getBackground());
            }

            setFont(table.getFont());

            if (hasFocus) {
                Border border = null;
                if (isSelected) {
                    border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
                }
                if (border == null) {
                    border = UIManager.getBorder("Table.focusCellHighlightBorder");
                }
                setBorder(border);

                if (!isSelected && table.isCellEditable(row, column)) {
                    Color col;
                    col = UIManager.getColor("Table.focusCellForeground");
                    if (col != null) {
                        super.setForeground(col);
                    }
                    col = UIManager.getColor("Table.focusCellBackground");
                    if (col != null) {
                        super.setBackground(col);
                    }
                }
            } else {
                if (System.getSecurityManager() != null) {
                    setBorder(new EmptyBorder(1, 1, 1, 1));
                } else {
                    setBorder(new EmptyBorder(1, 1, 1, 1));
                }
            }
            
            JLabel label = (JLabel) value;
            setText(label.getText());
            if (column == 0) {
                setIcon(label.getIcon());
            }
            return this;
        }
    }

    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        detectedPopupMenu = new javax.swing.JPopupMenu();
        detectedDelete = new javax.swing.JMenuItem();
        detectedDeleteAll = new javax.swing.JMenuItem();
        detectedSkip = new javax.swing.JMenuItem();
        detectedQuarantine = new javax.swing.JMenuItem();
        suspectedPopupMenu = new javax.swing.JPopupMenu();
        suspectedDelete = new javax.swing.JMenuItem();
        suspectedDeleteAll = new javax.swing.JMenuItem();
        suspectedSkip = new javax.swing.JMenuItem();
        suspectedQuarantine = new javax.swing.JMenuItem();
        imgPanel = new javax.swing.JPanel();
        img = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        nowScanningLbl = new javax.swing.JLabel();
        locationLbl = new javax.swing.JLabel();
        nowScanning = new javax.swing.JLabel();
        location = new javax.swing.JLabel();
        progress = new javax.swing.JProgressBar();
        scannedLbl = new javax.swing.JLabel();
        detectedLbl = new javax.swing.JLabel();
        suspectedLbl = new javax.swing.JLabel();
        scanned = new javax.swing.JLabel();
        detected = new javax.swing.JLabel();
        suspected = new javax.swing.JLabel();
        startTimeLbl = new javax.swing.JLabel();
        durationLbl = new javax.swing.JLabel();
        finishTimeLbl = new javax.swing.JLabel();
        startTime = new javax.swing.JLabel();
        hourLbl = new javax.swing.JLabel();
        finishTime = new javax.swing.JLabel();
        duration1 = new javax.swing.JLabel();
        minLbl = new javax.swing.JLabel();
        secLbl = new javax.swing.JLabel();
        duration4 = new javax.swing.JLabel();
        tabs = new javax.swing.JTabbedPane();
        detectedTab = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        detectedTable = new javax.swing.JTable();
        detectedAction = new javax.swing.JButton();
        detectedMsg = new javax.swing.JLabel();
        suspectedTab = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        suspectedTable = new javax.swing.JTable();
        suspectedAction = new javax.swing.JButton();
        suspectedMsg = new javax.swing.JLabel();
        statTab = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        statTable = new javax.swing.JTable();
        btnPanel = new javax.swing.JPanel();
        close = new javax.swing.JButton();
        stop = new javax.swing.JButton();
        pause = new javax.swing.JButton();
        prev = new javax.swing.JButton();
        next = new javax.swing.JButton();

        detectedDelete.setMnemonic('D');
        detectedDelete.setText("Delete");
        detectedDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detectedDeleteActionPerformed(evt);
            }
        });
        detectedPopupMenu.add(detectedDelete);

        detectedDeleteAll.setMnemonic('A');
        detectedDeleteAll.setText("Delete All");
        detectedDeleteAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detectedDeleteAllActionPerformed(evt);
            }
        });
        detectedPopupMenu.add(detectedDeleteAll);

        detectedSkip.setMnemonic('S');
        detectedSkip.setText("Skip");
        detectedSkip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detectedSkipActionPerformed(evt);
            }
        });
        detectedPopupMenu.add(detectedSkip);

        detectedQuarantine.setMnemonic('Q');
        detectedQuarantine.setText("Quarantine");
        detectedQuarantine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detectedQuarantineActionPerformed(evt);
            }
        });
        detectedPopupMenu.add(detectedQuarantine);

        suspectedDelete.setMnemonic('D');
        suspectedDelete.setText("Delete");
        suspectedDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suspectedDeleteActionPerformed(evt);
            }
        });
        suspectedPopupMenu.add(suspectedDelete);

        suspectedDeleteAll.setMnemonic('A');
        suspectedDeleteAll.setText("Delete All");
        suspectedDeleteAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suspectedDeleteAllActionPerformed(evt);
            }
        });
        suspectedPopupMenu.add(suspectedDeleteAll);

        suspectedSkip.setMnemonic('S');
        suspectedSkip.setText("Skip");
        suspectedSkip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suspectedSkipActionPerformed(evt);
            }
        });
        suspectedPopupMenu.add(suspectedSkip);

        suspectedQuarantine.setMnemonic('Q');
        suspectedQuarantine.setText("Quarantine");
        suspectedQuarantine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suspectedQuarantineActionPerformed(evt);
            }
        });
        suspectedPopupMenu.add(suspectedQuarantine);

        setBackground(new java.awt.Color(224, 223, 227));
        setName("scanPanel"); // NOI18N

        imgPanel.setBackground(new java.awt.Color(224, 223, 227));

        img.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/scan.gif"))); // NOI18N

        javax.swing.GroupLayout imgPanelLayout = new javax.swing.GroupLayout(imgPanel);
        imgPanel.setLayout(imgPanelLayout);
        imgPanelLayout.setHorizontalGroup(
            imgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(img, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        imgPanelLayout.setVerticalGroup(
            imgPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(imgPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(img))
        );

        mainPanel.setBackground(new java.awt.Color(224, 223, 227));

        nowScanningLbl.setText("Now Scanning:");

        locationLbl.setText("Location:");

        progress.setDoubleBuffered(true);
        progress.setStringPainted(true);

        scannedLbl.setText("Scanned:");

        detectedLbl.setText("Detected:");

        suspectedLbl.setText("Suspected:");

        scanned.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        scanned.setText("0");

        detected.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        detected.setText("0");

        suspected.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        suspected.setText("0");

        startTimeLbl.setText("Start Time:");

        durationLbl.setText("Duration");

        finishTimeLbl.setText("Finish Time:");

        startTime.setText("0");

        hourLbl.setText("00");

        finishTime.setText("Unknown");

        duration1.setText(":");

        minLbl.setText("00");

        secLbl.setText("00");

        duration4.setText(":");

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progress, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(locationLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nowScanningLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(location, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nowScanning, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scannedLbl)
                            .addComponent(detectedLbl)
                            .addComponent(suspectedLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(detected, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scanned, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(suspected, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(startTimeLbl)
                            .addComponent(durationLbl)
                            .addComponent(finishTimeLbl))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(startTime, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(finishTime, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addComponent(hourLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(duration1)
                                .addGap(0, 0, 0)
                                .addComponent(minLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(duration4)
                                .addGap(0, 0, 0)
                                .addComponent(secLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {detected, scanned, suspected});

        mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {finishTime, startTime});

        mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {durationLbl, finishTimeLbl, startTimeLbl});

        mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {detectedLbl, scannedLbl, suspectedLbl});

        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nowScanningLbl)
                    .addComponent(nowScanning, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(locationLbl)
                    .addComponent(location, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(scannedLbl)
                        .addGap(2, 2, 2)
                        .addComponent(detectedLbl)
                        .addGap(2, 2, 2)
                        .addComponent(suspectedLbl))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(scanned, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(detected, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(suspected))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(startTimeLbl)
                        .addGap(2, 2, 2)
                        .addComponent(durationLbl)
                        .addGap(2, 2, 2)
                        .addComponent(finishTimeLbl))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(startTime, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(hourLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(duration1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(minLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(duration4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(secLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addComponent(finishTime)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabs.setBackground(new java.awt.Color(255, 255, 255));
        tabs.setDoubleBuffered(true);

        detectedTab.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));

        detectedTable.setAutoCreateRowSorter(true);
        detectedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Status", "File"
            }
        ) {
            Class[] types = new Class [] {
                javax.swing.JLabel.class, javax.swing.JLabel.class
            };

            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        detectedTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        detectedTable.setDoubleBuffered(true);
        detectedTable.setFillsViewportHeight(true);
        detectedTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        detectedTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        detectedTable.setShowHorizontalLines(false);
        detectedTable.setShowVerticalLines(false);
        detectedTable.getTableHeader().setReorderingAllowed(false);
        detectedTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                detectedTableMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                detectedTableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(detectedTable);
        detectedTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        detectedTable.setDefaultRenderer(JLabel.class, new JLabelRenderer());

        detectedAction.setText("Action...");
        detectedAction.setEnabled(false);
        detectedAction.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                detectedActionMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout detectedTabLayout = new javax.swing.GroupLayout(detectedTab);
        detectedTab.setLayout(detectedTabLayout);
        detectedTabLayout.setHorizontalGroup(
            detectedTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detectedTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(detectedTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
                    .addGroup(detectedTabLayout.createSequentialGroup()
                        .addComponent(detectedMsg, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(detectedAction)))
                .addContainerGap())
        );
        detectedTabLayout.setVerticalGroup(
            detectedTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detectedTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(detectedTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(detectedMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(detectedAction, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tabs.addTab("Detected", detectedTab);

        suspectedTab.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));

        suspectedTable.setAutoCreateRowSorter(true);
        suspectedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Status", "File"
            }
        ) {
            Class[] types = new Class [] {
                javax.swing.JLabel.class, javax.swing.JLabel.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        suspectedTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        suspectedTable.setDoubleBuffered(true);
        suspectedTable.setFillsViewportHeight(true);
        suspectedTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        suspectedTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        suspectedTable.setShowHorizontalLines(false);
        suspectedTable.setShowVerticalLines(false);
        suspectedTable.getTableHeader().setReorderingAllowed(false);
        suspectedTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                suspectedTableMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                suspectedTableMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(suspectedTable);
        suspectedTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        suspectedTable.setDefaultRenderer(JLabel.class, new JLabelRenderer());

        suspectedAction.setText("Action...");
        suspectedAction.setEnabled(false);
        suspectedAction.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                suspectedActionMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout suspectedTabLayout = new javax.swing.GroupLayout(suspectedTab);
        suspectedTab.setLayout(suspectedTabLayout);
        suspectedTabLayout.setHorizontalGroup(
            suspectedTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, suspectedTabLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(suspectedTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
                    .addGroup(suspectedTabLayout.createSequentialGroup()
                        .addComponent(suspectedMsg, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(suspectedAction)))
                .addContainerGap())
        );
        suspectedTabLayout.setVerticalGroup(
            suspectedTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, suspectedTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(suspectedTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(suspectedAction)
                    .addComponent(suspectedMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tabs.addTab("Suspected", suspectedTab);

        statTab.setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane3.setBackground(new java.awt.Color(255, 255, 255));

        statTable.setAutoCreateRowSorter(true);
        statTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Drives", "Scanned", "Detected", "Suspected", "Deleted", "Quarantined", "Skipped"
            }
        ) {
            Class[] types = new Class [] {
                javax.swing.JLabel.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        statTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        statTable.setDoubleBuffered(true);
        statTable.setFillsViewportHeight(true);
        statTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        statTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        statTable.setShowHorizontalLines(false);
        statTable.setShowVerticalLines(false);
        statTable.getTableHeader().setReorderingAllowed(false);
        statTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                statTableMousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(statTable);
        statTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        javax.swing.GroupLayout statTabLayout = new javax.swing.GroupLayout(statTab);
        statTab.setLayout(statTabLayout);
        statTabLayout.setHorizontalGroup(
            statTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
                .addContainerGap())
        );
        statTabLayout.setVerticalGroup(
            statTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabs.addTab("Statistics", statTab);

        btnPanel.setBackground(new java.awt.Color(224, 223, 227));

        close.setText("Close");
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });

        stop.setText("Stop");
        stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopActionPerformed(evt);
            }
        });

        pause.setText("Pause");
        pause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseActionPerformed(evt);
            }
        });

        prev.setText("<<");
        prev.setToolTipText("View previous scan");
        prev.setEnabled(false);
        prev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevActionPerformed(evt);
            }
        });

        next.setText(">>");
        next.setToolTipText("View next scan");
        next.setEnabled(false);
        next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout btnPanelLayout = new javax.swing.GroupLayout(btnPanel);
        btnPanel.setLayout(btnPanelLayout);
        btnPanelLayout.setHorizontalGroup(
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, btnPanelLayout.createSequentialGroup()
                .addComponent(prev)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(next)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 274, Short.MAX_VALUE)
                .addComponent(pause)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(stop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(close, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        btnPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {close, pause, stop});

        btnPanelLayout.setVerticalGroup(
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(close)
                .addComponent(pause)
                .addComponent(stop)
                .addComponent(next)
                .addComponent(prev))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tabs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(imgPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(imgPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabs, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeActionPerformed
    scanFrame.setVisible(false);
}//GEN-LAST:event_closeActionPerformed

private void nextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextActionPerformed
    doScan.isCurrentlyShowing = false;
    scanFrame.panel.removeAll();
    scanFrame.panel.validate();
    scanFrame.panel.repaint();
    scanFrame.panel.add(nextScan);
    nextScan.doScan.isCurrentlyShowing = true;
    scanFrame.setTitle(nextScan.doScan.titleWithPercentage);
}//GEN-LAST:event_nextActionPerformed

private void prevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevActionPerformed
    doScan.isCurrentlyShowing = false;
    scanFrame.panel.removeAll();
    scanFrame.panel.validate();
    scanFrame.panel.repaint();
    scanFrame.panel.add(previousScan);
    previousScan.doScan.isCurrentlyShowing = true;
    scanFrame.setTitle(previousScan.doScan.titleWithPercentage);
}//GEN-LAST:event_prevActionPerformed

private void stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopActionPerformed
    shouldStopScanning = true;
    stop.setEnabled(false);
    pause.setEnabled(false);
    if (doScan.detectedTableModel.getRowCount() > 0) {
        detectedAction.setEnabled(true);
    }
    if (doScan.suspectedTableModel.getRowCount() > 0) {
        suspectedAction.setEnabled(true);
    }
    if (doScan.slideInScanPanel != null) {
        doScan.slideInScanPanel.pause.removeMouseListener(doScan.slideInScanPanel.pause.getMouseListeners()[0]);
        doScan.slideInScanPanel.pause.removeMouseListener(doScan.slideInScanPanel.pause.getMouseListeners()[0]);
        doScan.slideInScanPanel.pause.setForeground(Color.DARK_GRAY);
        doScan.slideInScanPanel.pause.setText("<html>Pause</html>");
        doScan.slideInScanPanel.pause.setCursor(Cursor.getDefaultCursor());
        doScan.slideInScanPanel.stop.removeMouseListener(doScan.slideInScanPanel.stop.getMouseListeners()[0]);
        doScan.slideInScanPanel.stop.removeMouseListener(doScan.slideInScanPanel.stop.getMouseListeners()[0]);
        doScan.slideInScanPanel.stop.setForeground(Color.DARK_GRAY);
        doScan.slideInScanPanel.stop.setText("<html>Stop</html>");
        doScan.slideInScanPanel.stop.setCursor(Cursor.getDefaultCursor());        
    }
}//GEN-LAST:event_stopActionPerformed

private void pauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseActionPerformed
    if (shouldPauseScanning) {
        doScan.resume();
        if (doScan.slideInScanPanel != null) {
            doScan.slideInScanPanel.isPaused = false;
            doScan.slideInScanPanel.pause.setText("<html><u>Pause</u></html");
        }
    } else {
        shouldPauseScanning = true;
        pause.setText("Resume");
        pause.setEnabled(false);
        stop.setEnabled(false);
        if (doScan.slideInScanPanel != null) {
            doScan.slideInScanPanel.isPaused = true;
            doScan.slideInScanPanel.pause.setText("<html><u>Resume</u></html");
        }
    }    
}//GEN-LAST:event_pauseActionPerformed

private void detectedTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_detectedTableMousePressed
    if (SwingUtilities.isRightMouseButton(evt)) {
        int currentRow = detectedTable.rowAtPoint(evt.getPoint());
        detectedTable.getSelectionModel().addSelectionInterval(currentRow, currentRow);
        detectedDelete.setEnabled(true);
        detectedDeleteAll.setVisible(false);
        detectedQuarantine.setEnabled(true);
        detectedSkip.setEnabled(true);
    } else if ((detectedTable.rowAtPoint(evt.getPoint()) == -1)
                && (detectedTable.getSelectedRowCount() != 0)) {
        detectedTable.removeRowSelectionInterval(0, detectedTable.getRowCount() - 1);
    }
}//GEN-LAST:event_detectedTableMousePressed

private void detectedTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_detectedTableMouseReleased
    if (evt.isPopupTrigger() && detectedAction.isEnabled()
                        && (detectedTable.rowAtPoint(evt.getPoint()) != -1)) {
        if (detectedPopupMenu.getComponentCount() > 3) {
            detectedPopupMenu.getComponent(1).setVisible(false);
        }
        detectedPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }
}//GEN-LAST:event_detectedTableMouseReleased

private void detectedActionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_detectedActionMouseClicked
    if (SwingUtilities.isLeftMouseButton(evt) && detectedAction.isEnabled()) {
        if (detectedTable.getSelectedRowCount() == 0) {
            detectedDelete.setEnabled(false);
            detectedQuarantine.setEnabled(false);
            detectedSkip.setEnabled(false);
        } else {            
            detectedDelete.setEnabled(true);
            detectedQuarantine.setEnabled(true);
            detectedSkip.setEnabled(true);
        }
        detectedDeleteAll.setVisible(true);
        detectedDeleteAll.setEnabled(true);
        detectedPopupMenu.show(detectedAction, (detectedAction.getWidth() - 89) / 2,
                        detectedAction.getHeight() - 59);
    }
}//GEN-LAST:event_detectedActionMouseClicked

private void suspectedTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_suspectedTableMousePressed
    if (SwingUtilities.isRightMouseButton(evt)) {
        int currentRow = suspectedTable.rowAtPoint(evt.getPoint());
        suspectedTable.getSelectionModel().addSelectionInterval(currentRow, currentRow);
        suspectedDelete.setEnabled(true);
        suspectedDelete.setEnabled(true);
        suspectedDeleteAll.setVisible(false);
        suspectedQuarantine.setEnabled(true);
        suspectedSkip.setEnabled(true);
    } else if ((suspectedTable.rowAtPoint(evt.getPoint()) == -1)
                && (suspectedTable.getSelectedRowCount() != 0)) {
        suspectedTable.removeRowSelectionInterval(0, suspectedTable.getRowCount() - 1);
    }
}//GEN-LAST:event_suspectedTableMousePressed

private void suspectedTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_suspectedTableMouseReleased
    if (evt.isPopupTrigger() && suspectedAction.isEnabled()
                        && (suspectedTable.rowAtPoint(evt.getPoint()) != -1)) {
        if (suspectedPopupMenu.getComponentCount() > 3) {
            suspectedPopupMenu.getComponent(1).setVisible(false);
        }
        suspectedPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
    }
}//GEN-LAST:event_suspectedTableMouseReleased

private void suspectedActionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_suspectedActionMouseClicked
    if (SwingUtilities.isLeftMouseButton(evt) && suspectedAction.isEnabled()) {
        if (suspectedTable.getSelectedRowCount() == 0) {
            suspectedDelete.setEnabled(false);
            suspectedQuarantine.setEnabled(false);
            suspectedSkip.setEnabled(false);
        } else {
            suspectedDelete.setEnabled(true);
            suspectedQuarantine.setEnabled(true);
            suspectedSkip.setEnabled(true);
        }
        suspectedDeleteAll.setVisible(true);
        suspectedDeleteAll.setEnabled(true);
        suspectedPopupMenu.show(suspectedAction, (suspectedAction.getWidth() - 89) / 2,
                        suspectedAction.getHeight() - 59);
    }
}//GEN-LAST:event_suspectedActionMouseClicked

private void detectedDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detectedDeleteActionPerformed
    new DetectedDeleteAction();
}//GEN-LAST:event_detectedDeleteActionPerformed

private void detectedSkipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detectedSkipActionPerformed
    class DetectedSkipAction implements Runnable {
        DetectedSkipAction() {
            new Thread(this).start();
        }
        
        public void run() {
            JLabel status;
            int[] selectedRowsIndexes = detectedTable.getSelectedRows();    
            for (int i = 0; i < detectedTable.getSelectedRowCount(); i++) {
                // Check whether the file is already deleted or quarantined
                if (!doScan.detectedFiles.get(selectedRowsIndexes[i]).exists()) {
                    continue;
                }

                // Check whether the file is already skipped
                JLabel label = (JLabel) doScan.detectedTableModel.getValueAt(selectedRowsIndexes[i], 0);
                if (label.getText().equalsIgnoreCase("Skipped")) {
                    continue;
                }

                // Update status
                status = new JLabel("Skipped",
                    new javax.swing.ImageIcon(getClass().getResource("/images/deleted.png")),
                    SwingConstants.LEADING);
                doScan.detectedTableModel.setValueAt(status, selectedRowsIndexes[i], 0);
                doScan.statTableModel.setValueAt(++doScan.totalFilesSkipped, 0, 6);

                // Update number of files skipped for each drive
                try {
                    for (int j = 1; j < statTable.getRowCount(); j++) {
                        JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                        if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                .equalsIgnoreCase(doScan.detectedFiles.get(selectedRowsIndexes[i])
                                .getCanonicalPath().substring(0, 1))) {
                            int skippedFiles = (Integer) doScan.statTableModel.getValueAt(j, 6);
                            doScan.statTableModel.setValueAt(++skippedFiles, j, 6);
                            break;
                        }
                    }
                } catch (IOException iOException) {}            
            }
        }        
    }
    
    new DetectedSkipAction();
}//GEN-LAST:event_detectedSkipActionPerformed

private void detectedQuarantineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detectedQuarantineActionPerformed
    class DetectedQuarantineAction implements Runnable {
        DetectedQuarantineAction() {
            new Thread(this).start();
        }
        
        public void run() {
            JLabel status;
            int[] selectedRowsIndexes = detectedTable.getSelectedRows();
            File quarantine = new File("Quarantine");
            if (!quarantine.exists()) {
                quarantine.mkdir();
            }

            BufferedInputStream bin = null;
            BufferedOutputStream bout = null;
            for (int i = 0; i < detectedTable.getSelectedRowCount(); i++) {
                try {
                    // Check whether the file is already quarantined
                    File currFile = doScan.detectedFiles.get(selectedRowsIndexes[i]);
                    if (!currFile.exists()) {
                        continue;
                    }            

                    // Check whether the file is already skipped; if skipped, then decrement number of skipped files                    
                    JLabel label = (JLabel) doScan.detectedTableModel.getValueAt(selectedRowsIndexes[i], 0);
                    if (label.getText().equalsIgnoreCase("Skipped")) {
                        doScan.statTableModel.setValueAt(--doScan.totalFilesSkipped, 0, 6);
                        // Update number of files skipped for each drive
                        try {
                            for (int j = 1; j < statTable.getRowCount(); j++) {
                                JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                                if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                        .equalsIgnoreCase(doScan.detectedFiles.get(selectedRowsIndexes[i])
                                        .getCanonicalPath().substring(0, 1))) {
                                    int skippedFiles = (Integer) doScan.statTableModel.getValueAt(j, 6);
                                    doScan.statTableModel.setValueAt(--skippedFiles, j, 6);
                                    break;
                                }
                            }
                        } catch (IOException iOException) {}
                    }

                    // Quarantine file & update status
                    bin = new BufferedInputStream(new FileInputStream(currFile));
                    bout = new BufferedOutputStream(new FileOutputStream("Quarantine/"
                            + currFile.getName().substring(0, currFile.getName().length() - 4)
                            + "~" + System.currentTimeMillis()
                            + currFile.getName().substring(currFile.getName().length() - 4)));            
                    while (bin.available() > 0) {
                        bout.write(bin.read());
                    }
                    bin.close();
                    currFile.delete();

                    status = new JLabel("Quarantined",
                        new javax.swing.ImageIcon(getClass().getResource("/images/deleted.png")),
                        SwingConstants.LEADING);
                    doScan.detectedTableModel.setValueAt(status, selectedRowsIndexes[i], 0);
                    doScan.statTableModel.setValueAt(++doScan.totalFilesQuarantined, 0, 5);

                    // Update number of files deleted for each drive
                    try {
                        for (int j = 1; j < statTable.getRowCount(); j++) {
                            JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                            if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                    .equalsIgnoreCase(currFile.getCanonicalPath().substring(0, 1))) {
                                int quarantinedFiles = (Integer) doScan.statTableModel.getValueAt(j, 5);
                                doScan.statTableModel.setValueAt(++quarantinedFiles, j, 5);
                                break;
                            }
                        }
                    } catch (IOException iOException) {}

                } catch (Exception e) {
                    status = new JLabel("Cannot Quarantine",
                        new javax.swing.ImageIcon(getClass().getResource("/images/detected.png")),
                        SwingConstants.LEADING);
                    status.setToolTipText(e.getMessage());
                    doScan.detectedTableModel.setValueAt(status, selectedRowsIndexes[i], 0);
                    detectedMsg.setText("<html><font color = 'red'>Some errors occurred. For details, hover "
                        + "mouse on the 'Cannot Delete' messages and pause for a while.</font></html>");
                    detectedMsg.setIcon(null);
                    doScan.statTableModel.setValueAt(++doScan.totalFilesSkipped, 0, 4);

                    // Update number of files skipped for each drive
                    try {
                        for (int j = 1; j < statTable.getRowCount(); j++) {
                            JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                            if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                    .equalsIgnoreCase(doScan.detectedFiles.get(selectedRowsIndexes[i])
                                    .getCanonicalPath().substring(0, 1))) {
                                int skippedFiles = (Integer) doScan.statTableModel.getValueAt(j, 6);
                                doScan.statTableModel.setValueAt(++skippedFiles, j, 6);
                                break;
                            }
                        }
                    } catch (Exception Exception) {}

                } finally {
                    try {
                        bin.close();
                        bout.close();
                    } catch (Exception e) {}
                }
            }
        }        
    }
    
    new DetectedQuarantineAction();
}//GEN-LAST:event_detectedQuarantineActionPerformed

private void suspectedDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suspectedDeleteActionPerformed
    new SuspectedDeleteAction();
}//GEN-LAST:event_suspectedDeleteActionPerformed

private void suspectedSkipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suspectedSkipActionPerformed
    class SuspectedSkipAction implements Runnable {
        SuspectedSkipAction() {
            new Thread(this).start();
        }
        
        public void run() {
            JLabel status;
            int[] selectedRowsIndexes = suspectedTable.getSelectedRows();
            for (int i = 0; i < suspectedTable.getSelectedRowCount(); i++) {
                if (!doScan.suspectedFiles.get(selectedRowsIndexes[i]).exists()) {
                    continue;
                }

                // Check whether the file is already skipped
                JLabel label = (JLabel) doScan.suspectedTableModel.getValueAt(selectedRowsIndexes[i], 0);
                if (label.getText().equalsIgnoreCase("Skipped")) {
                    continue;
                }

                // Update status        
                status = new JLabel("Skipped",
                    new javax.swing.ImageIcon(getClass().getResource("/images/deleted.png")),
                    SwingConstants.LEADING);
                doScan.suspectedTableModel.setValueAt(status, selectedRowsIndexes[i], 0);
                doScan.statTableModel.setValueAt(++doScan.totalFilesSkipped, 0, 6);

                // Update number of files skipped for each drive
                try {
                    for (int j = 1; j < statTable.getRowCount(); j++) {
                        JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                        if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                .equalsIgnoreCase(doScan.suspectedFiles.get(selectedRowsIndexes[i])
                                .getCanonicalPath().substring(0, 1))) {
                            int skippedFiles = (Integer) doScan.statTableModel.getValueAt(j, 6);
                            doScan.statTableModel.setValueAt(++skippedFiles, j, 6);
                            break;
                        }
                    }
                } catch (IOException iOException) {}            
            }
        }        
    }
    
    new SuspectedSkipAction();
}//GEN-LAST:event_suspectedSkipActionPerformed

private void suspectedQuarantineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suspectedQuarantineActionPerformed
    class SuspectedQuarantineAction implements Runnable {
        SuspectedQuarantineAction() {
            new Thread(this).start();
        }
        
        public void run() {
            JLabel status;
            int[] selectedRowsIndexes = suspectedTable.getSelectedRows();
            File quarantine = new File("Quarantine");
            if (!quarantine.exists()) {
                quarantine.mkdir();
            }

            BufferedInputStream bin = null;
            BufferedOutputStream bout = null;
            for (int i = 0; i < suspectedTable.getSelectedRowCount(); i++) {
                try {
                    File currFile = doScan.suspectedFiles.get(selectedRowsIndexes[i]);
                    if (!currFile.exists()) {
                        continue;
                    }

                    // Check whether the file is already skipped; if skipped, then decrement number of skipped files                    
                    JLabel label = (JLabel) doScan.suspectedTableModel.getValueAt(selectedRowsIndexes[i], 0);
                    if (label.getText().equalsIgnoreCase("Skipped")) {
                        doScan.statTableModel.setValueAt(--doScan.totalFilesSkipped, 0, 6);
                        // Update number of files skipped for each drive
                        try {
                            for (int j = 1; j < statTable.getRowCount(); j++) {
                                JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                                if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                        .equalsIgnoreCase(doScan.suspectedFiles.get(selectedRowsIndexes[i])
                                        .getCanonicalPath().substring(0, 1))) {
                                    int skippedFiles = (Integer) doScan.statTableModel.getValueAt(j, 6);
                                    doScan.statTableModel.setValueAt(--skippedFiles, j, 6);
                                    break;
                                }
                            }
                        } catch (IOException iOException) {}
                    }

                    // Quarantine file & update status            
                    bin = new BufferedInputStream(new FileInputStream(currFile));
                    bout = new BufferedOutputStream(new FileOutputStream("Quarantine/"
                            + currFile.getName().substring(0, currFile.getName().length() - 4)
                            + "~" + System.currentTimeMillis()
                            + currFile.getName().substring(currFile.getName().length() - 4)));            
                    while (bin.available() > 0) {
                        bout.write(bin.read());
                    }
                    bin.close();
                    currFile.delete();

                    status = new JLabel("Quarantined",
                        new javax.swing.ImageIcon(getClass().getResource("/images/deleted.png")),
                        SwingConstants.LEADING);
                    doScan.suspectedTableModel.setValueAt(status, selectedRowsIndexes[i], 0);
                    doScan.statTableModel.setValueAt(++doScan.totalFilesQuarantined, 0, 5);

                    // Update number of files deleted for each drive
                    try {
                        for (int j = 1; j < statTable.getRowCount(); j++) {
                            JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                            if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                    .equalsIgnoreCase(currFile.getCanonicalPath().substring(0, 1))) {
                                int quarantinedFiles = (Integer) doScan.statTableModel.getValueAt(j, 5);
                                doScan.statTableModel.setValueAt(++quarantinedFiles, j, 5);
                                break;
                            }
                        }
                    } catch (IOException iOException) {}

                } catch (Exception e) {
                    status = new JLabel("Cannot Quarantine",
                        new javax.swing.ImageIcon(getClass().getResource("/images/detected.png")),
                        SwingConstants.LEADING);
                    status.setToolTipText(e.getMessage());
                    doScan.suspectedTableModel.setValueAt(status, selectedRowsIndexes[i], 0);
                    suspectedMsg.setText("<html><font color = 'red'>Some errors occurred. For details, hover "
                        + "mouse on the 'Cannot Delete' messages and pause for a while.</font></html>");
                    suspectedMsg.setIcon(null);
                    doScan.statTableModel.setValueAt(++doScan.totalFilesSkipped, 0, 4);

                    // Update number of files skipped for each drive
                    try {
                        for (int j = 1; j < statTable.getRowCount(); j++) {
                            JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                            if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                    .equalsIgnoreCase(doScan.suspectedFiles.get(selectedRowsIndexes[i])
                                    .getCanonicalPath().substring(0, 1))) {
                                int skippedFiles = (Integer) doScan.statTableModel.getValueAt(j, 6);
                                doScan.statTableModel.setValueAt(++skippedFiles, j, 6);
                                break;
                            }
                        }
                    } catch (Exception Exception) {}

                } finally {
                    try {
                        bin.close();
                        bout.close();
                    } catch (Exception e) {}
                }
            }
        }        
    }
    
    new SuspectedQuarantineAction();
}//GEN-LAST:event_suspectedQuarantineActionPerformed

private void statTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_statTableMousePressed
    if ((statTable.rowAtPoint(evt.getPoint()) == -1)
                && (statTable.getSelectedRowCount() != 0)) {
        statTable.removeRowSelectionInterval(0, statTable.getRowCount() - 1);
    }
}//GEN-LAST:event_statTableMousePressed

private void detectedDeleteAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detectedDeleteAllActionPerformed
    detectedTable.selectAll();
    new DetectedDeleteAction();
}//GEN-LAST:event_detectedDeleteAllActionPerformed

private void suspectedDeleteAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suspectedDeleteAllActionPerformed
    suspectedTable.selectAll();
    new SuspectedDeleteAction();
}//GEN-LAST:event_suspectedDeleteAllActionPerformed

class DetectedDeleteAction implements Runnable {
    DetectedDeleteAction() {
        new Thread(this).start();
    }

    public void run() {
        JLabel status;
        int[] selectedRowsIndexes = detectedTable.getSelectedRows();
        for (int i = 0; i < detectedTable.getSelectedRowCount(); i++) {
            try {
                // Check whether the file is already deleted or quarantined
                if (doScan.detectedFiles.get(selectedRowsIndexes[i]).exists()) {
                    doScan.detectedFiles.get(selectedRowsIndexes[i]).delete();
                } else {
                    continue;
                }

                // Check whether the file is already skipped; if skipped, then decrement number of skipped files
                JLabel label = (JLabel) doScan.detectedTableModel.getValueAt(selectedRowsIndexes[i], 0);
                if (label.getText().equalsIgnoreCase("Skipped")) {
                    doScan.statTableModel.setValueAt(--doScan.totalFilesSkipped, 0, 6);
                    // Update number of files skipped for each drive
                    try {
                        for (int j = 1; j < statTable.getRowCount(); j++) {
                            JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                            if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                    .equalsIgnoreCase(doScan.detectedFiles.get(selectedRowsIndexes[i])
                                    .getCanonicalPath().substring(0, 1))) {
                                int skippedFiles = (Integer) doScan.statTableModel.getValueAt(j, 6);
                                doScan.statTableModel.setValueAt(--skippedFiles, j, 6);
                                break;
                            }
                        }
                    } catch (IOException iOException) {}
                }

                // Update status
                status = new JLabel("Deleted",
                    new javax.swing.ImageIcon(getClass().getResource("/images/deleted.png")),
                    SwingConstants.LEADING);
                doScan.detectedTableModel.setValueAt(status, selectedRowsIndexes[i], 0);
                doScan.statTableModel.setValueAt(++doScan.totalFilesDeleted, 0, 4);

                // Update number of files deleted for each drive
                try {
                    for (int j = 1; j < statTable.getRowCount(); j++) {
                        JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                        if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                .equalsIgnoreCase(doScan.detectedFiles.get(selectedRowsIndexes[i])
                                .getCanonicalPath().substring(0, 1))) {
                            int deletedFiles = (Integer) doScan.statTableModel.getValueAt(j, 4);
                            doScan.statTableModel.setValueAt(++deletedFiles, j, 4);
                            break;
                        }
                    }
                } catch (IOException iOException) {}

            } catch (Exception e) {
                status = new JLabel("Cannot Delete",
                    new javax.swing.ImageIcon(getClass().getResource("/images/detected.png")),
                    SwingConstants.LEADING);
                status.setToolTipText(e.getMessage());
                doScan.detectedTableModel.setValueAt(status, selectedRowsIndexes[i], 0);
                detectedMsg.setText("<html><font color = 'red'>Some errors occurred. For details, hover "
                    + "mouse on the 'Cannot Delete' messages and pause for a while.</font></html>");
                detectedMsg.setIcon(null);
                doScan.statTableModel.setValueAt(++doScan.totalFilesSkipped, 0, 4);

                // Update number of files skipped for each drive
                try {
                    for (int j = 1; j < statTable.getRowCount(); j++) {
                        JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                        if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                .equalsIgnoreCase(doScan.detectedFiles.get(selectedRowsIndexes[i])
                                .getCanonicalPath().substring(0, 1))) {
                            int skippedFiles = (Integer) doScan.statTableModel.getValueAt(j, 6);
                            doScan.statTableModel.setValueAt(++skippedFiles, j, 6);
                            break;
                        }
                    }
                } catch (IOException iOException) {}

            }
        }
    }        
}

class SuspectedDeleteAction implements Runnable {
    SuspectedDeleteAction() {
        new Thread(this).start();
    }

    public void run() {
        JLabel status;
        int[] selectedRowsIndexes = suspectedTable.getSelectedRows();
        for (int i = 0; i < suspectedTable.getSelectedRowCount(); i++) {
            try {
                // Check whether the file is already deleted or quarantined
                if (doScan.suspectedFiles.get(selectedRowsIndexes[i]).exists()) {
                    doScan.suspectedFiles.get(selectedRowsIndexes[i]).delete();
                } else {
                    continue;
                }

                // Check whether the file is already skipped; if skipped, then decrement number of skipped files
                JLabel label = (JLabel) doScan.suspectedTableModel.getValueAt(selectedRowsIndexes[i], 0);
                if (label.getText().equalsIgnoreCase("Skipped")) {
                    doScan.statTableModel.setValueAt(--doScan.totalFilesSkipped, 0, 6);
                    // Update number of files skipped for each drive
                    try {
                        for (int j = 1; j < statTable.getRowCount(); j++) {
                            JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                            if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                    .equalsIgnoreCase(doScan.suspectedFiles.get(selectedRowsIndexes[i])
                                    .getCanonicalPath().substring(0, 1))) {
                                int skippedFiles = (Integer) doScan.statTableModel.getValueAt(j, 6);
                                doScan.statTableModel.setValueAt(--skippedFiles, j, 6);
                                break;
                            }
                        }
                    } catch (IOException iOException) {}
                }

                // Update status
                status = new JLabel("Deleted",
                    new javax.swing.ImageIcon(getClass().getResource("/images/deleted.png")),
                    SwingConstants.LEADING);
                doScan.suspectedTableModel.setValueAt(status, selectedRowsIndexes[i], 0);
                doScan.statTableModel.setValueAt(++doScan.totalFilesDeleted, 0, 4);

                // Update number of files deleted for each drive
                try {
                    for (int j = 1; j < statTable.getRowCount(); j++) {
                        JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                        if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                .equalsIgnoreCase(doScan.suspectedFiles.get(selectedRowsIndexes[i])
                                .getCanonicalPath().substring(0, 1))) {
                            int deletedFiles = (Integer) doScan.statTableModel.getValueAt(j, 4);
                            doScan.statTableModel.setValueAt(++deletedFiles, j, 4);
                            break;
                        }
                    }
                } catch (IOException iOException) {}

            } catch (Exception e) {
                status = new JLabel("Cannot Delete",
                    new javax.swing.ImageIcon(getClass().getResource("/images/suspected.png")),
                    SwingConstants.LEADING);
                status.setToolTipText(e.getMessage());
                doScan.suspectedTableModel.setValueAt(status, selectedRowsIndexes[i], 0);
                suspectedMsg.setText("<html><font color = 'red'>Some errors occurred. For details, hover "
                    + "mouse on the 'Cannot Delete' messages and pause for a while.</font></html>");
                suspectedMsg.setIcon(null);
                doScan.statTableModel.setValueAt(++doScan.totalFilesSkipped, 0, 4);

                // Update number of files skipped for each drive
                try {
                    for (int j = 1; j < statTable.getRowCount(); j++) {
                        JLabel driveLabel = (JLabel) doScan.statTableModel.getValueAt(j, 0);
                        if (Character.toString(driveLabel.getText().charAt(driveLabel.getText().length() - 3))
                                .equalsIgnoreCase(doScan.suspectedFiles.get(selectedRowsIndexes[i])
                                .getCanonicalPath().substring(0, 1))) {
                            int skippedFiles = (Integer) doScan.statTableModel.getValueAt(j, 6);
                            doScan.statTableModel.setValueAt(++skippedFiles, j, 6);
                            break;
                        }
                    }
                } catch (IOException iOException) {}

            }
        }
    }        
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JPanel btnPanel;
    private javax.swing.JButton close;
    private javax.swing.JLabel detected;
    protected javax.swing.JButton detectedAction;
    private javax.swing.JMenuItem detectedDelete;
    private javax.swing.JMenuItem detectedDeleteAll;
    private javax.swing.JLabel detectedLbl;
    private javax.swing.JLabel detectedMsg;
    private javax.swing.JPopupMenu detectedPopupMenu;
    private javax.swing.JMenuItem detectedQuarantine;
    private javax.swing.JMenuItem detectedSkip;
    private javax.swing.JPanel detectedTab;
    private javax.swing.JTable detectedTable;
    private javax.swing.JLabel duration1;
    private javax.swing.JLabel duration4;
    private javax.swing.JLabel durationLbl;
    private javax.swing.JLabel finishTime;
    private javax.swing.JLabel finishTimeLbl;
    private javax.swing.JLabel hourLbl;
    private javax.swing.JLabel img;
    private javax.swing.JPanel imgPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel location;
    private javax.swing.JLabel locationLbl;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel minLbl;
    protected javax.swing.JButton next;
    private javax.swing.JLabel nowScanning;
    private javax.swing.JLabel nowScanningLbl;
    protected javax.swing.JButton pause;
    private javax.swing.JButton prev;
    private javax.swing.JProgressBar progress;
    private javax.swing.JLabel scanned;
    private javax.swing.JLabel scannedLbl;
    private javax.swing.JLabel secLbl;
    private javax.swing.JLabel startTime;
    private javax.swing.JLabel startTimeLbl;
    private javax.swing.JPanel statTab;
    private javax.swing.JTable statTable;
    protected javax.swing.JButton stop;
    private javax.swing.JLabel suspected;
    protected javax.swing.JButton suspectedAction;
    private javax.swing.JMenuItem suspectedDelete;
    private javax.swing.JMenuItem suspectedDeleteAll;
    private javax.swing.JLabel suspectedLbl;
    private javax.swing.JLabel suspectedMsg;
    private javax.swing.JPopupMenu suspectedPopupMenu;
    private javax.swing.JMenuItem suspectedQuarantine;
    private javax.swing.JMenuItem suspectedSkip;
    private javax.swing.JPanel suspectedTab;
    private javax.swing.JTable suspectedTable;
    private javax.swing.JTabbedPane tabs;
    // End of variables declaration//GEN-END:variables

}