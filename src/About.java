/*
 * About.java
 *
 * Created on July 23, 2008, 12:37 PM
 */

import java.net.URI;
import java.awt.*;

/**
 *
 * @author  Sharafat
 */
public class About extends javax.swing.JPanel {

    /** Creates new form About */
    public About() {
        initComponents();
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        licence = new javax.swing.JLabel();
        website = new javax.swing.JLabel();
        email = new javax.swing.JLabel();
        heading = new javax.swing.JLabel();
        logo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        licence.setText("<html><b>License Agreement:</b><br>You are free to copy, modify and redistribute this program provided that - <ol><li>Commercial transactions might not be involved.</li> <li>If you modify this program, you must include at least the following line in the About section:<br><p align='center'>\"Original software credit: Sharafat Ibn Mollah Mosharraf (www.sharafat.co.uk)\"</p></li></ol></p></html>");

        website.setText("<html><b>www.sharafat.co.uk</b></html>");
        website.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                websiteMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                websiteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                websiteMouseExited(evt);
            }
        });

        email.setText("sharafat_8271@yahoo.co.uk");
        email.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                emailMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                emailMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                emailMouseExited(evt);
            }
        });

        heading.setText("<html><h2>USB Worm Protection</h2><b>Version:</b> 1.2 Build 2021.07.26</html>");
        heading.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/USBWP_logo.png"))); // NOI18N
        logo.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel1.setText("<html><b>Crafted with Love by:</b><br>Sharafat Ibn Mollah Mosharraf<br>Founder & CEO, Eximus Technologies<br>www.eximustech.com</html>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(website)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(logo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(heading, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(licence, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logo)
                    .addComponent(heading))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(email)
                .addGap(0, 0, 0)
                .addComponent(website)
                .addGap(18, 18, 18)
                .addComponent(licence)
                .addContainerGap())
        );

        website.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }// </editor-fold>//GEN-END:initComponents

private void emailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emailMouseClicked
    if (desktop.isSupported(Desktop.Action.MAIL)) {      
        try {
            uri = new URI("mailto", "sharafat_8271@yahoo.co.uk", null);
            desktop.mail(uri);
        } catch (Exception ex) {}
    }
}//GEN-LAST:event_emailMouseClicked

private void emailMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emailMouseEntered
    if (desktop.isSupported(Desktop.Action.MAIL)) {
        email.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        email.setForeground(Color.RED);
    }
}//GEN-LAST:event_emailMouseEntered

private void websiteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_websiteMouseEntered
    if (desktop.isSupported(Desktop.Action.MAIL)) {
        website.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        website.setForeground(Color.RED);
    }
}//GEN-LAST:event_websiteMouseEntered

private void websiteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_websiteMouseClicked
    if (desktop.isSupported(Desktop.Action.BROWSE)) {      
        try {
            uri = new URI("http://www.sharafat.co.uk");
            desktop.browse(uri);
        } catch (Exception ex) {}
    }
}//GEN-LAST:event_websiteMouseClicked

private void websiteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_websiteMouseExited
    if (desktop != null) {
        website.setForeground(Color.BLACK);
    }
}//GEN-LAST:event_websiteMouseExited

private void emailMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emailMouseExited
    if (desktop != null) {
        email.setForeground(Color.BLACK);
    }
}//GEN-LAST:event_emailMouseExited


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel email;
    private javax.swing.JLabel heading;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel licence;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel website;
    // End of variables declaration//GEN-END:variables
    
    // Custom Field declarations
    private Desktop desktop;
    private URI uri;
}
