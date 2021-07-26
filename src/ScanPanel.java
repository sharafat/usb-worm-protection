/*
 * ScanPanel.java
 *
 * Created on July 23, 2008, 1:23 PM
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.GroupLayout.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import javax.swing.event.*;

/**
 *
 * @author  Sharafat
 */
public class ScanPanel extends javax.swing.JPanel {
    private ScansManager scansManager;
    private File[] currentScannableDrives;    
    protected File[] drivesToBeScanned;
    private JCheckBox[] ch;
    private JLabel[] label;
    
    /** Creates new form ScanPanel
     * @param scansManager The ScansManager object
     */
    public ScanPanel(ScansManager scansManager) {
        this.scansManager = scansManager;
        initComponents();
    }

    protected void displayDrives() {
        File[] currentDrives = File.listRoots();
        FileSystemView fileSystem = FileSystemView.getFileSystemView();
        String driveDescription;
        
        // Get scannable Drives
        int noOfScannableDrives = 0;
        for (File drive : currentDrives) {
            driveDescription = fileSystem.getSystemTypeDescription(drive);
            if (driveDescription == null) {
                continue;
            }
            if ((driveDescription.equalsIgnoreCase("Local Disk")) ||
                (driveDescription.equalsIgnoreCase("Removable Disk")) ||
                (driveDescription.equalsIgnoreCase("USB Drive")) ||
                (driveDescription.equalsIgnoreCase("Network Drive"))){
                    noOfScannableDrives++;
            }
        }
        currentScannableDrives = new File[noOfScannableDrives];
        int count = 0;
        for (File drive : currentDrives) {
            driveDescription = fileSystem.getSystemTypeDescription(drive);
            if (driveDescription == null) {
                continue;
            }
            if ((driveDescription.equalsIgnoreCase("Local Disk")) ||
                (driveDescription.equalsIgnoreCase("Removable Disk")) ||
                (driveDescription.equalsIgnoreCase("USB Drive")) ||
                (driveDescription.equalsIgnoreCase("Network Drive"))){
                    currentScannableDrives[count++] = drive;
            }
        }
        ch = new JCheckBox[currentScannableDrives.length];
        label = new JLabel[currentScannableDrives.length];
        
        // Display the drives        
        for (int i = 0; i < currentScannableDrives.length; i++) {
            ch[i] = new JCheckBox();
            ch[i].setBackground(Color.WHITE);
            
            // Add ChangeListener to check box to enable/disable the Start Scan button
            final int j = i; // Workaround for using this index inside inner class                        
            ch[i].addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (ch[j].isSelected()) {
                        startScan.setEnabled(true);
                    } else {
                        boolean isAnyCheckBoxSelected = false;
                        for (int k = 0; k < ch.length; k++) {
                            if (ch[k].isSelected()) {
                                isAnyCheckBoxSelected = true;
                                break;
                            }
                        }
                        if (!isAnyCheckBoxSelected) {
                            startScan.setEnabled(false);
                        }
                    }
                }                
            });
            
            // Add MouseListener to label to simulate click on check box
            label[i] = new JLabel(
                    fileSystem.getSystemDisplayName(currentScannableDrives[i]),
                    fileSystem.getSystemIcon(currentScannableDrives[i]), JLabel.LEFT);
            label[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (ch[j].isSelected()) {
                        ch[j].setSelected(false);
                    } else {
                        ch[j].setSelected(true);
                    }
                }
            });
        }
        
        drivesListPanel.removeAll();
        startScan.setEnabled(false);
                
        GroupLayout layout = new GroupLayout(drivesListPanel);
        drivesListPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        SequentialGroup seqGroup1 = layout.createSequentialGroup();
        SequentialGroup seqGroup2 = layout.createSequentialGroup();
        ParallelGroup horParGroup1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        ParallelGroup horParGroup2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);        

        for (int i = 0; i < currentScannableDrives.length; i++) {
            horParGroup1 = horParGroup1.addComponent(ch[i]);
            horParGroup2 = horParGroup2.addComponent(label[i]);
            seqGroup2.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(ch[i]).addComponent(label[i]));            
        }
        layout.setHorizontalGroup(seqGroup1.addGroup(horParGroup1).addGroup(horParGroup2));
        layout.setVerticalGroup(seqGroup2);
        
        validate();
        
        // Enable View Quarantined Files button if necessary
        File quarantineFolder = new File("Quarantine");
        String[] quarantinedFiles = quarantineFolder.list();
        if ((quarantinedFiles != null) && (quarantinedFiles.length != 0)) {
            viewQuarantine.setEnabled(true);
            quarantineLbl.setText("There are " + quarantinedFiles.length + " file(s) quarantined.");
        } else {
            viewQuarantine.setEnabled(false);
            quarantineLbl.setText("There is no file quarantined.");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        driveScanPanel = new javax.swing.JPanel();
        refreshListBtn = new javax.swing.JButton();
        deselectAllBtn = new javax.swing.JButton();
        selectAllBtn = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        drivesListPanel = new javax.swing.JPanel();
        startScan = new javax.swing.JButton();
        scansPanel = new javax.swing.JPanel();
        scanningsLbl = new javax.swing.JLabel();
        scanDetails = new javax.swing.JButton();
        quarantinePanel = new javax.swing.JPanel();
        viewQuarantine = new javax.swing.JButton();
        quarantineLbl = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        driveScanPanel.setBackground(new java.awt.Color(255, 255, 255));
        driveScanPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Drives to Scan"));

        refreshListBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/reload.png"))); // NOI18N
        refreshListBtn.setMnemonic('R');
        refreshListBtn.setText("Refresh List");
        refreshListBtn.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        refreshListBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshListBtnActionPerformed(evt);
            }
        });

        deselectAllBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/deselectAll.png"))); // NOI18N
        deselectAllBtn.setMnemonic('D');
        deselectAllBtn.setText("Deselect All");
        deselectAllBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        deselectAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deselectAllBtnActionPerformed(evt);
            }
        });

        selectAllBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/selectAll.png"))); // NOI18N
        selectAllBtn.setMnemonic('E');
        selectAllBtn.setText("Select All");
        selectAllBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        selectAllBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllBtnActionPerformed(evt);
            }
        });

        scrollPane.setBackground(new java.awt.Color(255, 255, 255));

        drivesListPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout drivesListPanelLayout = new javax.swing.GroupLayout(drivesListPanel);
        drivesListPanel.setLayout(drivesListPanelLayout);
        drivesListPanelLayout.setHorizontalGroup(
            drivesListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 232, Short.MAX_VALUE)
        );
        drivesListPanelLayout.setVerticalGroup(
            drivesListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 516, Short.MAX_VALUE)
        );

        scrollPane.setViewportView(drivesListPanel);

        startScan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/scan.png"))); // NOI18N
        startScan.setMnemonic('S');
        startScan.setText("<html><b>Start Scan</b></html>");
        startScan.setEnabled(false);
        startScan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        startScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startScanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout driveScanPanelLayout = new javax.swing.GroupLayout(driveScanPanel);
        driveScanPanel.setLayout(driveScanPanelLayout);
        driveScanPanelLayout.setHorizontalGroup(
            driveScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(driveScanPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(driveScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startScan)
                    .addComponent(deselectAllBtn)
                    .addComponent(selectAllBtn)
                    .addComponent(refreshListBtn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        driveScanPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {deselectAllBtn, refreshListBtn, selectAllBtn, startScan});

        driveScanPanelLayout.setVerticalGroup(
            driveScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(driveScanPanelLayout.createSequentialGroup()
                .addGroup(driveScanPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, driveScanPanelLayout.createSequentialGroup()
                        .addComponent(refreshListBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectAllBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deselectAllBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startScan)))
                .addContainerGap())
        );

        driveScanPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {deselectAllBtn, refreshListBtn, selectAllBtn, startScan});

        scansPanel.setBackground(new java.awt.Color(255, 255, 255));
        scansPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Scan Details"));

        scanningsLbl.setText("There are currently 0 scan(s) in progress.");

        scanDetails.setBackground(new java.awt.Color(255, 255, 255));
        scanDetails.setMnemonic('V');
        scanDetails.setText("View Scanning Details...");
        scanDetails.setEnabled(false);
        scanDetails.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        scanDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scanDetailsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout scansPanelLayout = new javax.swing.GroupLayout(scansPanel);
        scansPanel.setLayout(scansPanelLayout);
        scansPanelLayout.setHorizontalGroup(
            scansPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scansPanelLayout.createSequentialGroup()
                .addComponent(scanningsLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, scansPanelLayout.createSequentialGroup()
                .addContainerGap(242, Short.MAX_VALUE)
                .addComponent(scanDetails))
        );
        scansPanelLayout.setVerticalGroup(
            scansPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scansPanelLayout.createSequentialGroup()
                .addComponent(scanningsLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scanDetails)
                .addContainerGap())
        );

        quarantinePanel.setBackground(new java.awt.Color(255, 255, 255));
        quarantinePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Quarantine"));

        viewQuarantine.setBackground(new java.awt.Color(255, 255, 255));
        viewQuarantine.setMnemonic('M');
        viewQuarantine.setText("View Quarantined Files");
        viewQuarantine.setEnabled(false);
        viewQuarantine.setPreferredSize(new java.awt.Dimension(147, 23));
        viewQuarantine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewQuarantineActionPerformed(evt);
            }
        });

        quarantineLbl.setText("There is no file quarantined.");

        javax.swing.GroupLayout quarantinePanelLayout = new javax.swing.GroupLayout(quarantinePanel);
        quarantinePanel.setLayout(quarantinePanelLayout);
        quarantinePanelLayout.setHorizontalGroup(
            quarantinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(quarantineLbl, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, quarantinePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(viewQuarantine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        quarantinePanelLayout.setVerticalGroup(
            quarantinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(quarantinePanelLayout.createSequentialGroup()
                .addComponent(quarantineLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(viewQuarantine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scansPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(driveScanPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(quarantinePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(driveScanPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scansPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(quarantinePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void refreshListBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshListBtnActionPerformed
    drivesListPanel.removeAll();
    repaint();
    displayDrives();
    startScan.setEnabled(false);
}//GEN-LAST:event_refreshListBtnActionPerformed

private void selectAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllBtnActionPerformed
    for (int i = 0; i < currentScannableDrives.length; i++) {
        ch[i].setSelected(true);
    }
    startScan.setEnabled(true);
}//GEN-LAST:event_selectAllBtnActionPerformed

private void deselectAllBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deselectAllBtnActionPerformed
    for (int i = 0; i < currentScannableDrives.length; i++) {
        ch[i].setSelected(false);
    }
    startScan.setEnabled(false);
}//GEN-LAST:event_deselectAllBtnActionPerformed

private void startScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startScanActionPerformed
    // Get the drives to be scanned
    int count = 0;
    for (int i = 0; i < currentScannableDrives.length; i++) {
        if (ch[i].isSelected()) {
            count++;
        }
    }
    drivesToBeScanned = new File[count];
    count = 0;
    for (int i = 0; i < currentScannableDrives.length; i++) {
        if (ch[i].isSelected()) {
            drivesToBeScanned[count++] = currentScannableDrives[i];
        }
    }
    
    // Call ScansManager to start scanning
    scansManager.performScan(drivesToBeScanned);
}//GEN-LAST:event_startScanActionPerformed

private void scanDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scanDetailsActionPerformed
    if (!ScansManager.scanFrame.isVisible()) {
        ScansManager.scanFrame.panel.removeAll();
        ScansManager.scanFrame.validate();
        ScansManager.scanFrame.panel.repaint();
        ScansManager.scanFrame.panel.add(ScansManager.scanQueue.get(0));
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        ScansManager.scanFrame.setLocation(((screenDim.width - ScansManager.scanFrame.getWidth()) / 2),
                        ((screenDim.height - ScansManager.scanFrame.getHeight()) / 2));
        ScansManager.scanFrame.setVisible(true);
    } else {
        ScansManager.scanFrame.setExtendedState(Frame.NORMAL);
        ScansManager.scanFrame.toFront();
    }
}//GEN-LAST:event_scanDetailsActionPerformed

private void viewQuarantineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewQuarantineActionPerformed
    File quarantineFolder = new File("Quarantine");
    if (quarantineFolder.exists()) {
        try {
            Runtime.getRuntime().exec("cmd.exe /C explorer \""
                    + quarantineFolder.getCanonicalPath() + "\"");
        } catch (Exception e) {}
    }    
}//GEN-LAST:event_viewQuarantineActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deselectAllBtn;
    private javax.swing.JPanel driveScanPanel;
    private javax.swing.JPanel drivesListPanel;
    private javax.swing.JLabel quarantineLbl;
    private javax.swing.JPanel quarantinePanel;
    private javax.swing.JButton refreshListBtn;
    protected static javax.swing.JButton scanDetails;
    protected static javax.swing.JLabel scanningsLbl;
    private javax.swing.JPanel scansPanel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton selectAllBtn;
    private javax.swing.JButton startScan;
    private javax.swing.JButton viewQuarantine;
    // End of variables declaration//GEN-END:variables

}
