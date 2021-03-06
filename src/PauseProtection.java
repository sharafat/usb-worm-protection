/*
 * PauseProtection.java
 *
 * Created on July 25, 2008, 1:42 PM
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

/**
 *
 * @author  Sharafat
 */
public class PauseProtection extends javax.swing.JDialog {
    private boolean[] isProtectionEnabled;
    private MenuItem protectionStatus;
    private DetectRemovableDevices detectRemDev;
    private ScansManager scansManager;
    protected Timer timer;

    /** Creates new form PauseProtection */
    public PauseProtection(Frame parent, boolean modal, Object[] param) {
        super(parent, modal);
        isProtectionEnabled = (boolean[]) param[0];
        protectionStatus = (MenuItem) param[1];
        detectRemDev = (DetectRemovableDevices) param[2];
        scansManager = (ScansManager) param[3];
        initComponents();
    }
    
    private void pauseProtection() {        
        isProtectionEnabled[0] = false;
        protectionStatus.setLabel("Resume Protection");
        TrayIcon[] trayIcon = SystemTray.getSystemTray().getTrayIcons();
        trayIcon[0].setImage(new ImageIcon(Main.class.getResource(
                "/images/disabledTrayIcon.png"), "Tray Icon (Disabled)").getImage());                            
        trayIcon[0].setToolTip("USB Worm Protection (Protection Disabled)");
        detectRemDev.disableProtection();
    }
        

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        warning = new javax.swing.JLabel();
        cancel = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        inPanel = new javax.swing.JLayeredPane();
        time = new javax.swing.JComboBox();
        inLabel = new javax.swing.JLabel();
        inHeadingLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        bgInPanel = new javax.swing.JLabel();
        nextPanel = new javax.swing.JLayeredPane();
        nextLabel = new javax.swing.JLabel();
        nextLabelHeading = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        bgNextPanel = new javax.swing.JLabel();
        userRequestPanel = new javax.swing.JLayeredPane();
        userRequestLabel = new javax.swing.JLabel();
        userRequestHeadingLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        bgUserReqPanel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("USB Worm Protection");
        setName("pauseDialog"); // NOI18N
        setResizable(false);

        warning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/warning.png"))); // NOI18N
        warning.setText("<html>Protection will be suspended.<br>Enable protection again:</html");
        warning.setIconTextGap(10);

        cancel.setMnemonic('C');
        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));

        inPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                inPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                inPanelMouseExited(evt);
            }
        });

        time.setMaximumRowCount(9);
        time.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1 minute", "3 minutes", "5 minutes", "10 minutes", "15 minutes", "30 minutes", "1 hour", "3 hours", "5 hours" }));
        time.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeActionPerformed(evt);
            }
        });
        time.setBounds(60, 10, 156, 20);
        inPanel.add(time, javax.swing.JLayeredPane.DEFAULT_LAYER);
        time.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        inLabel.setText("<html>Protection will be suspended; it will be resumed automatically in 1 minute.</html>");
        inLabel.setBounds(40, 30, 188, 32);
        inPanel.add(inLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        inHeadingLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        inHeadingLabel.setForeground(new java.awt.Color(0, 0, 153));
        inHeadingLabel.setText("In");
        inHeadingLabel.setBounds(40, 10, 12, 14);
        inPanel.add(inHeadingLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pauseDialog.png"))); // NOI18N
        jLabel1.setBounds(10, 10, 20, 20);
        inPanel.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        bgInPanel.setBounds(0, 0, 254, 70);
        inPanel.add(bgInPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        nextPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nextPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nextPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nextPanelMouseExited(evt);
            }
        });

        nextLabel.setText("<html>Protection will be suspended until application restart.</html>");
        nextLabel.setBounds(40, 30, 188, 32);
        nextPanel.add(nextLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        nextLabelHeading.setFont(new java.awt.Font("Tahoma", 1, 11));
        nextLabelHeading.setForeground(new java.awt.Color(0, 0, 153));
        nextLabelHeading.setText("At next program restart");
        nextLabelHeading.setBounds(40, 10, 137, 14);
        nextPanel.add(nextLabelHeading, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pauseDialog.png"))); // NOI18N
        jLabel2.setBounds(10, 10, 20, 20);
        nextPanel.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);
        bgNextPanel.setBounds(0, 0, 254, 70);
        nextPanel.add(bgNextPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        userRequestPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userRequestPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                userRequestPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                userRequestPanelMouseExited(evt);
            }
        });

        userRequestLabel.setText("<html>Protection will be suspended until the user enables it again.</html>");
        userRequestLabel.setBounds(40, 30, 188, 32);
        userRequestPanel.add(userRequestLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        userRequestHeadingLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        userRequestHeadingLabel.setForeground(new java.awt.Color(0, 0, 153));
        userRequestHeadingLabel.setText("By user request only");
        userRequestHeadingLabel.setBounds(40, 10, 116, 14);
        userRequestPanel.add(userRequestHeadingLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pauseDialog.png"))); // NOI18N
        jLabel3.setBounds(10, 10, 20, 20);
        userRequestPanel.add(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        bgUserReqPanel.setBounds(0, 0, 254, 70);
        userRequestPanel.add(bgUserReqPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(userRequestPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                    .addComponent(nextPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nextPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(userRequestPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(warning)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(cancel)
                        .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addComponent(warning)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
    dispose();
}//GEN-LAST:event_cancelActionPerformed

private void timeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeActionPerformed
inLabel.setText("<html>Protection will be suspended; it will be resumed automatically in "
            + time.getSelectedItem().toString() + ".</html>");
}//GEN-LAST:event_timeActionPerformed

private void inPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inPanelMouseEntered
    setCursor(new Cursor(Cursor.HAND_CURSOR));
    bgInPanel.setIcon(new ImageIcon(getClass().getResource("/images/overlay.png")));
}//GEN-LAST:event_inPanelMouseEntered

private void inPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inPanelMouseExited
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    bgInPanel.setIcon(null);
}//GEN-LAST:event_inPanelMouseExited

private void nextPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextPanelMouseEntered
    setCursor(new Cursor(Cursor.HAND_CURSOR));
    bgNextPanel.setIcon(new ImageIcon(getClass().getResource("/images/overlay.png")));
}//GEN-LAST:event_nextPanelMouseEntered

private void nextPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextPanelMouseExited
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    bgNextPanel.setIcon(null);
}//GEN-LAST:event_nextPanelMouseExited

private void userRequestPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userRequestPanelMouseEntered
    setCursor(new Cursor(Cursor.HAND_CURSOR));
    bgUserReqPanel.setIcon(new ImageIcon(getClass().getResource("/images/overlay.png")));
}//GEN-LAST:event_userRequestPanelMouseEntered

private void userRequestPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userRequestPanelMouseExited
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    bgUserReqPanel.setIcon(null);
}//GEN-LAST:event_userRequestPanelMouseExited

private void inPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inPanelMouseClicked
    // Get duration of disabling protection
    int duration = 0;
    switch (time.getSelectedIndex()) {
        case 0 : duration = 1 * 60 * 1000; break;       // 1 min.
        case 1 : duration = 3 * 60 * 1000; break;       // 3 min.
        case 2 : duration = 5 * 60 * 1000; break;       // 5 min.
        case 3 : duration = 10 * 60 * 1000; break;      // 10 min.
        case 4 : duration = 15 * 60 * 1000; break;      // 15 min.
        case 5 : duration = 30 * 60 * 1000; break;      // 30 min
        case 6 : duration = 1 * 60 * 60 * 1000; break;  // 1 hour
        case 7 : duration = 3 * 60 * 60 * 1000; break;  // 3 hour
        case 8 : duration = 5 * 60 * 60 * 1000;         // 5 hour
    }
    
    // Start timer
    ActionListener timerTaskPerformer = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            isProtectionEnabled[0] = true;
            protectionStatus.setLabel("Disable Protection");
            TrayIcon[] trayIcon = SystemTray.getSystemTray().getTrayIcons();
            trayIcon[0].setImage(new ImageIcon(Main.class.getResource("/images/icon.png"), 
                    "Tray Icon").getImage());
            trayIcon[0].setToolTip("USB Worm Protection");
            detectRemDev = new DetectRemovableDevices(scansManager);
            
            // Notify user of the enabling of protection
            SlideInDialog protectDisabledMsg = new SlideInDialog();
            protectDisabledMsg.bodyPanel.add(new JLabel(
                    "Protection against worms is enabled.",
                    new ImageIcon(getClass().getResource("/images/info.png")),
                    SwingConstants.CENTER));
            new SlideInNotification(protectDisabledMsg);
        }
    };
    timer = new Timer(duration, timerTaskPerformer);
    timer.setRepeats(false);
    timer.start();
    
    // Write in file the protection status
    writeProtectionStatus(0);
    
    // Disable protection
    pauseProtection();
    
    // Dispose the dialog
    dispose();
}//GEN-LAST:event_inPanelMouseClicked

private void nextPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nextPanelMouseClicked
    // Write in file the protection status
    writeProtectionStatus(0);
    
    // Disable protection
    pauseProtection();
    
    // Dispose the dialog
    dispose();
}//GEN-LAST:event_nextPanelMouseClicked

private void userRequestPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userRequestPanelMouseClicked
    // Write in file the protection status
    writeProtectionStatus(1);
    
    // Disable protection
    pauseProtection();
    
    // Dispose the dialog
    dispose();
}//GEN-LAST:event_userRequestPanelMouseClicked

private void writeProtectionStatus(int status) {
    File prefFile = new File("preferences.dat");                
    boolean prefFileExists = prefFile.exists();
    Preferences preferences = new Preferences();
    if (prefFileExists) {        
        ObjectInputStream obin = null;
        ObjectOutputStream obout = null;
        try {
            obin = new ObjectInputStream(new FileInputStream("preferences.dat"));
            preferences = (Preferences) obin.readObject();
            obin.close();
            preferences.protectionEnableStatus = status;
            obout = new ObjectOutputStream(new FileOutputStream("preferences.dat"));
            obout.writeObject(preferences);
        } catch (Exception e) {
        } finally {
            try {
                obout.close();
                obin.close();
            } catch (Exception e) {}
        }
    } else {
        ObjectOutputStream obout = null;
        try {
            preferences.protectionEnableStatus = status;
            obout = new ObjectOutputStream(new FileOutputStream("preferences.dat"));
            obout.writeObject(preferences);
        } catch (Exception e) {
        } finally {
            try {
                obout.close();
            } catch (Exception e) {}
        }
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bgInPanel;
    private javax.swing.JLabel bgNextPanel;
    private javax.swing.JLabel bgUserReqPanel;
    private javax.swing.JButton cancel;
    private javax.swing.JLabel inHeadingLabel;
    private javax.swing.JLabel inLabel;
    private javax.swing.JLayeredPane inPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel nextLabel;
    private javax.swing.JLabel nextLabelHeading;
    private javax.swing.JLayeredPane nextPanel;
    private javax.swing.JComboBox time;
    private javax.swing.JLabel userRequestHeadingLabel;
    private javax.swing.JLabel userRequestLabel;
    private javax.swing.JLayeredPane userRequestPanel;
    private javax.swing.JLabel warning;
    // End of variables declaration//GEN-END:variables

}
