/*
 * ScanFrame.java
 *
 * Created on July 24, 2008, 8:51 AM
 */

import java.awt.Toolkit;

/**
 *
 * @author  Sharafat
 */
public class ScanFrame extends javax.swing.JFrame {

    /** Creates new form ScanFrame */
    public ScanFrame() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();

        setTitle("Scan");
        setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("images/icon.png")));
        setMinimumSize(new java.awt.Dimension(656, 520));
        setName("scanFrame"); // NOI18N

        panel.setPreferredSize(new java.awt.Dimension(0, 0));
        panel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 10, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 10, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JPanel panel;
    // End of variables declaration//GEN-END:variables

}
