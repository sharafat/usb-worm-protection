/*
 * MainWindow.java
 *
 * Created on July 23, 2008, 5:31 AM
 */

import java.awt.*;

/**
 *
 * @author  Sharafat
 */
public class MainWindow extends javax.swing.JFrame {
    About aboutPanel;
    ScanPanel scanPanel;
    PreferencesPanel prefPanel;

    /** Creates new form MainWindow
     * @param aboutPanel The About Panel
     * @param scanPanel The Scan Panel
     * @param prefPanel The Preferences Panel
     * @param scanFrame The Scan Frame
     */
    public MainWindow(About aboutPanel, ScanPanel scanPanel, PreferencesPanel prefPanel) {
        this.aboutPanel = aboutPanel;
        this.scanPanel = scanPanel;
        this.prefPanel = prefPanel;
        initComponents();
        panel.add(scanPanel);        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        scanLbl = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        prefLbl = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        aboutLbl = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        title = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setTitle("USB Worm Protection");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("images/icon.png")));
        setResizable(false);

        panel.setBackground(new java.awt.Color(255, 255, 255));
        panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        scanLbl.setBackground(new java.awt.Color(255, 255, 255));
        scanLbl.setFont(new java.awt.Font("Arial", 1, 12));
        scanLbl.setForeground(new java.awt.Color(255, 255, 255));
        scanLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/scan.png"))); // NOI18N
        scanLbl.setText("Scan");
        scanLbl.setIconTextGap(7);
        scanLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scanLblMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                scanLblMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                scanLblMouseExited(evt);
            }
        });
        scanLbl.setBounds(10, 0, 160, 27);
        jLayeredPane1.add(scanLbl, javax.swing.JLayeredPane.DEFAULT_LAYER);
        scanLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/header_blue.png"))); // NOI18N
        jLabel1.setBounds(0, 0, 170, 27);
        jLayeredPane1.add(jLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        prefLbl.setBackground(new java.awt.Color(255, 255, 255));
        prefLbl.setFont(new java.awt.Font("Arial", 0, 12));
        prefLbl.setForeground(new java.awt.Color(255, 255, 255));
        prefLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/preferences.png"))); // NOI18N
        prefLbl.setText("Preferences");
        prefLbl.setIconTextGap(7);
        prefLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                prefLblMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                prefLblMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                prefLblMouseExited(evt);
            }
        });
        prefLbl.setBounds(10, 0, 160, 25);
        jLayeredPane2.add(prefLbl, javax.swing.JLayeredPane.DEFAULT_LAYER);
        prefLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/header_blue.png"))); // NOI18N
        jLabel2.setBounds(0, 0, 170, 27);
        jLayeredPane2.add(jLabel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        aboutLbl.setBackground(new java.awt.Color(255, 255, 255));
        aboutLbl.setFont(new java.awt.Font("Arial", 0, 12));
        aboutLbl.setForeground(new java.awt.Color(255, 255, 255));
        aboutLbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/about.png"))); // NOI18N
        aboutLbl.setText("About");
        aboutLbl.setIconTextGap(7);
        aboutLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                aboutLblMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                aboutLblMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                aboutLblMouseExited(evt);
            }
        });
        aboutLbl.setBounds(10, 0, 160, 27);
        jLayeredPane3.add(aboutLbl, javax.swing.JLayeredPane.DEFAULT_LAYER);
        aboutLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/header_blue.png"))); // NOI18N
        jLabel3.setBounds(0, 0, 170, 27);
        jLayeredPane3.add(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        title.setBackground(new java.awt.Color(255, 255, 255));
        title.setFont(new java.awt.Font("Arial", 1, 12));
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/scan.png"))); // NOI18N
        title.setText("Scan");
        title.setIconTextGap(7);
        title.setBounds(6, 0, 430, 27);
        jLayeredPane4.add(title, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/header_blue_2.png"))); // NOI18N
        jLabel4.setBounds(0, 0, 436, 27);
        jLayeredPane4.add(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                    .addComponent(jLayeredPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .addComponent(jLayeredPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLayeredPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLayeredPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    protected void scanLabelClicked() {
        prefLbl.setFont(new Font("Arial", 0, 12));
        prefLbl.setText("Preferences");
        scanLbl.setFont(new Font("Arial", 1, 12));    
        aboutLbl.setFont(new Font("Arial", 0, 12));    
        aboutLbl.setText("About");

        title.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/images/scan.png")));
        title.setText("<html><b>Scan</b></html>");

        scanPanel.displayDrives();
        panel.removeAll();
        validate();
        panel.repaint();
        panel.add(scanPanel);
    }
    
    protected void preferencesLabelClicked() {
        prefLbl.setFont(new Font("Arial", 1, 12));
        scanLbl.setFont(new Font("Arial", 0, 12));
        scanLbl.setText("Scan");
        aboutLbl.setFont(new Font("Arial", 0, 12));    
        aboutLbl.setText("About");

        title.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/images/preferences.png")));
        title.setText("<html><b>Preferences</b></html>");

        panel.removeAll();
        validate();
        panel.repaint();
        panel.add(prefPanel);
    }
    
    protected void aboutLabelClicked() {
        prefLbl.setFont(new Font("Arial", 0, 12));
        prefLbl.setText("Preferences");
        scanLbl.setFont(new Font("Arial", 0, 12));
        scanLbl.setText("Scan");
        aboutLbl.setFont(new Font("Arial", 1, 12));        

        title.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/images/about.png")));
        title.setText("<html><b>About</b></html>");

        panel.removeAll();
        validate();
        panel.repaint();
        panel.add(aboutPanel);
    }
    
private void scanLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scanLblMouseClicked
    scanLabelClicked();
}//GEN-LAST:event_scanLblMouseClicked

private void prefLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prefLblMouseClicked
    preferencesLabelClicked();
}//GEN-LAST:event_prefLblMouseClicked

private void aboutLblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aboutLblMouseClicked
    aboutLabelClicked();
}//GEN-LAST:event_aboutLblMouseClicked

private void scanLblMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scanLblMouseEntered
    if (scanLbl.getFont().isBold()) {
        scanLbl.setText("<html><b><u>Scan</u></b></html>");
    } else {
        scanLbl.setText("<html><u>Scan</u></html>");
    }
}//GEN-LAST:event_scanLblMouseEntered

private void scanLblMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scanLblMouseExited
    if (scanLbl.getFont().isBold()) {
        scanLbl.setText("<html><b>Scan</b></html>");
    } else {
        scanLbl.setText("<html>Scan</html>");
    }
}//GEN-LAST:event_scanLblMouseExited

private void prefLblMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prefLblMouseEntered
    if (prefLbl.getFont().isBold()) {
        prefLbl.setText("<html><b><u>Preferences</u></b></html>");
    } else {
        prefLbl.setText("<html><u>Preferences</u></html>");
    }
}//GEN-LAST:event_prefLblMouseEntered

private void prefLblMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_prefLblMouseExited
    if (prefLbl.getFont().isBold()) {
        prefLbl.setText("<html><b>Preferences</b></html>");
    } else {
        prefLbl.setText("<html>Preferences</html>");
    }
}//GEN-LAST:event_prefLblMouseExited

private void aboutLblMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aboutLblMouseEntered
    if (aboutLbl.getFont().isBold()) {
        aboutLbl.setText("<html><b><u>About</u></b></html>");
    } else {
        aboutLbl.setText("<html><u>About</u></html>");
    }
}//GEN-LAST:event_aboutLblMouseEntered

private void aboutLblMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_aboutLblMouseExited
    if (aboutLbl.getFont().isBold()) {
        aboutLbl.setText("<html><b>About</b></html>");
    } else {
        aboutLbl.setText("<html>About</html>");
    }
}//GEN-LAST:event_aboutLblMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel aboutLbl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JLayeredPane jLayeredPane4;
    protected javax.swing.JPanel panel;
    private javax.swing.JLabel prefLbl;
    private javax.swing.JLabel scanLbl;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables

}
