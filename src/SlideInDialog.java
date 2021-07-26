/*
 * SlideInDialog.java
 *
 * Created on July 27, 2008, 2:11 AM
 */

import java.awt.Cursor;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author  Sharafat
 */
public class SlideInDialog extends javax.swing.JPanel {
    private JWindow holdingWindow;
    
    // Previous position of mouse pointer (for the case of dragging the slide-in-dialog)
    private int previousPosX;
    private int previousPosY;
    
    /** Creates new form SlideInDialog */
    public SlideInDialog() {
        initComponents();
    }

    /**
     * 
     * @param holdingWindow Reference of the window holding the slide-in dialog panel
     */
    public void setHoldingWindow(JWindow holdingWindow) {
        this.holdingWindow = holdingWindow;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titlePanel = new javax.swing.JLayeredPane();
        title = new javax.swing.JLabel();
        close = new javax.swing.JLabel();
        background = new javax.swing.JLabel();
        bodyPanel = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));

        titlePanel.setBackground(new java.awt.Color(255, 255, 255));
        titlePanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        titlePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                titlePanelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                titlePanelMouseReleased(evt);
            }
        });
        titlePanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                titlePanelMouseDragged(evt);
            }
        });

        title.setFont(new java.awt.Font("Tahoma", 1, 11));
        title.setForeground(new java.awt.Color(255, 255, 255));
        title.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon.png"))); // NOI18N
        title.setText("USB Worm Protection");
        title.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        title.setBounds(3, 0, 150, 20);
        titlePanel.add(title, javax.swing.JLayeredPane.DEFAULT_LAYER);

        close.setBackground(new java.awt.Color(255, 255, 255));
        close.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/disabledTrayIcon.png"))); // NOI18N
        close.setToolTipText("Hide This Dialog");
        close.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        close.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                closeMouseClicked(evt);
            }
        });
        close.setBounds(250, 0, 16, 20);
        titlePanel.add(close, javax.swing.JLayeredPane.DEFAULT_LAYER);

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/background_blue.png"))); // NOI18N
        background.setText("jLabel1");
        background.setBounds(0, 0, 268, 23);
        titlePanel.add(background, javax.swing.JLayeredPane.DEFAULT_LAYER);

        bodyPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(titlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
            .addComponent(bodyPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(bodyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void closeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_closeMouseClicked
      removeDialog();
}//GEN-LAST:event_closeMouseClicked

private void titlePanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_titlePanelMousePressed
    previousPosX = evt.getX();
    previousPosY = evt.getY();
    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
}//GEN-LAST:event_titlePanelMousePressed

private void titlePanelMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_titlePanelMouseDragged
    holdingWindow.setLocation(holdingWindow.getX() + evt.getX() - previousPosX,
            holdingWindow.getY() + evt.getY() - previousPosY);
}//GEN-LAST:event_titlePanelMouseDragged

private void titlePanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_titlePanelMouseReleased
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_titlePanelMouseReleased

protected void removeDialog() {
    while (holdingWindow.getLocation().y <= Toolkit.getDefaultToolkit().getScreenSize().height) {
        holdingWindow.setLocation(holdingWindow.getX(), holdingWindow.getY() + 1);
        try {
            Thread.sleep(SlideInNotification.ANIMATION_DELAY / 20);
        } catch (InterruptedException e) {}
    }
    holdingWindow.dispose(); 
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JLabel background;
    protected javax.swing.JPanel bodyPanel;
    private javax.swing.JLabel close;
    private javax.swing.JLabel title;
    private javax.swing.JLayeredPane titlePanel;
    // End of variables declaration//GEN-END:variables

}
