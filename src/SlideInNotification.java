import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class SlideInNotification {
    protected static final int ANIMATION_TIME = 500;
    protected static final float ANIMATION_TIME_F = (float) ANIMATION_TIME;                
    protected static final int ANIMATION_DELAY = 50;
    protected JWindow window;
    private JComponent contents;
    private AnimatingSheet animatingSheet;
    private Rectangle desktopBounds;
    private Dimension tempWindowSize;
    private Timer animationTimer;
    private int showX, startY;
    private long animationStart;

    /**
     * 
     * @param contents The slide-in dialog panel to be displayed
     */
    public SlideInNotification(SlideInDialog contents) {
        initDesktopBounds();
        setContents(contents);
        show();
    }

    protected void initDesktopBounds() {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        desktopBounds = env.getMaximumWindowBounds();
    }

    public void setContents(SlideInDialog contents) {
        this.contents = contents;
        JWindow tempWindow = new JWindow();
        tempWindow.getContentPane().add (contents);
        tempWindow.pack();
        tempWindowSize = tempWindow.getSize();
        tempWindow.getContentPane().removeAll();
        window = new JWindow();
        animatingSheet = new AnimatingSheet();
        animatingSheet.setSource(contents);
        window.getContentPane().add(animatingSheet);
        contents.setHoldingWindow(window);
    }

    /**
     * Create a window with an animating sheet, copy over its contents from 
     * the temp window and animate it. When done, remove animating sheet and 
     * add real contents
     * @param slideInDialogWidth Width of the slide-in dialog panel
     */
    public void show() {
        showX = desktopBounds.width - contents.getWidth() - 2;
        startY = desktopBounds.y + desktopBounds.height;

        ActionListener animationLogic = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - animationStart;                
                if (elapsed > ANIMATION_TIME) {
                    // Put real contents in window and show
                    window.getContentPane().removeAll();
                    window.getContentPane().add(contents);
                    window.pack();
                    window.setLocation(showX, startY - window.getSize().height);
                    window.setVisible(true);
                    window.repaint();
                    animationTimer.stop();
                    animationTimer = null;
                } else {
                    // Calculate percentage done
                    float progress = (float) elapsed / ANIMATION_TIME_F;                    
                    // Get height to show 
                    int animatingHeight = (int) (progress * tempWindowSize.getHeight());
                    animatingHeight = Math.max(animatingHeight, 1);
                    animatingSheet.setAnimatingHeight(animatingHeight);
                    window.pack();
                    window.setLocation(showX, startY - window.getHeight());                   
                    window.setVisible(true);
                    window.repaint();
                }
            }
        };
        animationTimer = new Timer(ANIMATION_DELAY, animationLogic);        
        animationStart = System.currentTimeMillis();
        animationTimer.start();
    }

    /** AnimatingSheet inner class
     * 
     */
    class AnimatingSheet extends JComponent {
        Dimension animatingSize = new Dimension (0, 1);
        JComponent source;
        BufferedImage offscreenImage;
        
        public AnimatingSheet() {
            setOpaque(true);
        }
        
        public void setSource(JComponent source) {
            this.source = source;
            animatingSize.width = source.getWidth( );
            makeOffscreenImage(source);
        }

        public void setAnimatingHeight(int height) {
            animatingSize.height = height;
            setSize(animatingSize);
        }
        
        private void makeOffscreenImage(JComponent source) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();              
            GraphicsConfiguration gfxConfig = ge.getDefaultScreenDevice().getDefaultConfiguration();              
            offscreenImage = gfxConfig.createCompatibleImage(source.getWidth(), source.getHeight());
            Graphics2D offscreenGraphics = (Graphics2D) offscreenImage.getGraphics();
              
            // Workaround for Windows OS problem
            offscreenGraphics.setColor(source.getBackground());
            offscreenGraphics.fillRect(0, 0, source.getWidth(), source.getHeight());
                                      
            // Paint from source to offscreen buffer
            source.paint(offscreenGraphics);
        }

        @Override
        public Dimension getPreferredSize() { return animatingSize; }

        @Override
        public Dimension getMinimumSize() { return animatingSize; }

        @Override
        public Dimension getMaximumSize() { return animatingSize; }

        @Override
        // Override to eliminate flicker from unnecessary clear
        public void update(Graphics g) {          
            paint(g);
        }

        @Override
        public void paint(Graphics g) {
            // Get the top-most n pixels of source and paint them into g, where n is height
            BufferedImage fragment = offscreenImage.getSubimage(
                    0, 0, source.getWidth(), animatingSize.height);
            g.drawImage(fragment, 0, 0, this);
        }
        
    }

}
