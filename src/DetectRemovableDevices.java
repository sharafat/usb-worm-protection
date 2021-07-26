import java.util.ArrayList;
import java.io.File;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Sharafat
 */
public class DetectRemovableDevices implements Runnable {
    public Thread t;
    private ScansManager scansManager;
    private boolean isProtectionEnabled = true;
    private ArrayList<File> permanentDrives = new ArrayList<File>(10);
    private ArrayList<File> newDrives = new ArrayList<File>(10);
    private ArrayList<File> detectedDrives = new ArrayList<File>(10);
    private File[] currentDrives;
        
    DetectRemovableDevices(ScansManager scansManager) {
        this.scansManager = scansManager;
        t = new Thread(this, "Removable Media Detection");
        t.start();
    }
    
    public void run() {
        // Detect currently attached drives and treat those as permanent drives
        File[] permaDrives = File.listRoots();
        for (File drive : permaDrives) {
            String description = FileSystemView.getFileSystemView().getSystemTypeDescription(drive);
            if (description != null && "Local Disk".equalsIgnoreCase(description)) {
                permanentDrives.add(drive);
            }            
        }        
        
        // Start detecting removable devices
        while (isProtectionEnabled) {
            continuouslyDetectRemovableDevices();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
    }

    private void continuouslyDetectRemovableDevices() {
        currentDrives = File.listRoots();        
        
        if ((currentDrives.length - detectedDrives.size()) < permanentDrives.size()) {
            boolean notPresentInDetectedDrives = true;
            for (int i = 0; i < detectedDrives.size(); i++) {
                File testPresenceOfDetectedDrives = detectedDrives.get(i);
                if (!testPresenceOfDetectedDrives.exists()) {
                    notPresentInDetectedDrives = false;
                    detectedDrives.remove(i);
                }
            }
             if (notPresentInDetectedDrives) {      
                for (int i = 0; i < permanentDrives.size(); i++) {
                    boolean isInCurrentDrives = false;
                    for (int j = 0; j < currentDrives.length; j++) {
                        if (permanentDrives.get(i).toString()
                                .equals(currentDrives[j].getAbsolutePath())) {
                            isInCurrentDrives = true;
                            break;
                        }
                    }
                    if (!isInCurrentDrives) {
                        permanentDrives.remove(i--);
                        if (i < 0) {
                            i = 0;
                        }
                    }
                }
            }
            return;
        } else if ((currentDrives.length - detectedDrives.size()) > permanentDrives.size()) {
            //Detect newly added drives
            int j = 0;
            for (int i = 0; i < currentDrives.length; i++) {                
                try {
                    if (!permanentDrives.get(j).toString()
                            .equals(currentDrives[i].toString())) {
                        boolean isAvailableInDetectedDrives = false;
                        for (int k = 0; k < detectedDrives.size(); k++) {
                            if (detectedDrives.get(k).toString()
                                    .equals(currentDrives[i].toString())) {
                                isAvailableInDetectedDrives = true;
                                break;
                            }
                        }
                        if (!isAvailableInDetectedDrives) {
                            newDrives.add(currentDrives[i]);                            
                        }
                        continue;
                    }
                } catch (IndexOutOfBoundsException e) {
                    boolean isAvailableInDetectedDrives = false;
                    for (int k = 0; k < detectedDrives.size(); k++) {                    
                        if (detectedDrives.get(k).toString().equals(currentDrives[i].toString())) {
                            isAvailableInDetectedDrives = true;
                            break;
                        }
                    }
                    if (!isAvailableInDetectedDrives) {
                        newDrives.add(currentDrives[i]);
                    }
                }
                j++;
            }
            
            //Start scanning newly added drives
            int newDrivesSize = newDrives.size();
            for (int i = 0; i < newDrivesSize; i++) {
                // Check for drives without disks, e.g., card readers without memory cards inserted
                if (FileSystemView.getFileSystemView().getSystemDisplayName(newDrives.get(0)).equals("")) {
                    detectedDrives.add(newDrives.get(0));
                    newDrives.remove(0);
                    continue;
                }

                // If the new drive is scannable, call ScansManager for scanning
                String driveType = FileSystemView.getFileSystemView()
                                                 .getSystemTypeDescription(newDrives.get(0));
                if (driveType != null
                        && (driveType.equalsIgnoreCase("Local Disk")
                        || driveType.equalsIgnoreCase("Removable Disk")
                        || driveType.equalsIgnoreCase("USB Drive")
                        || driveType.equalsIgnoreCase("Network Drive"))) {                    
                    scansManager.performScan(newDrives.get(0));
                }

                // Mark this drive as detected
                detectedDrives.add(newDrives.get(0));
                newDrives.remove(0);
            }            
        }

    }
    
    public void disableProtection(){
        isProtectionEnabled = false;
    }
    
    public void enableProtection(){
        isProtectionEnabled = true;
    }
    
}
