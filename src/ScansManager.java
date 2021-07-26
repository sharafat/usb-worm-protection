import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Sharafat
 */
public class ScansManager {
    // The main scan frame
    protected static ScanFrame scanFrame = new ScanFrame();       
    // Scan queue
    protected static ArrayList<Scan> scanQueue = new ArrayList<Scan>(5);
    // Number of scans currently running
    protected static int noOfRunningScans = 0;
    // The preferences
    private Object[] preferences; 
    
    
    /**
     * Constructs a new ScansManager object.
     * @param preferences The preferences put together in a single array of type Object
     */
    ScansManager(Object[] preferences) {
        this.preferences = preferences;
    }
    
    /**
     * Scans a removable media (single drive) when it is plugged in.
     * @param drive String containing the Drive letter of the drive to be scanned
     */
    public void performScan(File drive) {
        if (scanQueue.size() > 0) {
            scanQueue.add(new Scan(drive, scanFrame, preferences, scanQueue.get(scanQueue.size() - 1)));
            scanQueue.get(scanQueue.size() - 2).setNextScan(scanQueue.get(scanQueue.size() - 1));
            scanQueue.get(scanQueue.size() - 2).doScan.isCurrentlyShowing = false;
        } else {
            scanQueue.add(new Scan(drive, scanFrame, preferences, null));
        }
        noOfRunningScans++;
        ScanPanel.scanningsLbl.setText("There are currently "
                + noOfRunningScans + " scan(s) in progress.");
        ScanPanel.scanDetails.setEnabled(true);
    }
    
    /**
     * Scans the drives selected in the scan panel in main window.
     * @param drives Array of Strings containing the drive letters
     */
    public void performScan(File[] drives) {
        if (scanQueue.size() > 0) {
            scanQueue.add(new Scan(drives, scanFrame, preferences, scanQueue.get(scanQueue.size() - 1)));
            scanQueue.get(scanQueue.size() - 2).setNextScan(scanQueue.get(scanQueue.size() - 1));
            scanQueue.get(scanQueue.size() - 2).doScan.isCurrentlyShowing = false;
        } else {
            scanQueue.add(new Scan(drives, scanFrame, preferences, null));
        }
        noOfRunningScans++;
        ScanPanel.scanningsLbl.setText("There are currently "
                + noOfRunningScans + " scan(s) in progress.");
        ScanPanel.scanDetails.setEnabled(true);
    }
    
}
