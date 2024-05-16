package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DefaultImageViewPane extends JPanel implements ImageView {

    private ImageViewDelegate delegate;
    private BufferedImage img = null;

    public DefaultImageViewPane() {
    }

    @Override
    public Dimension getPreferredSize() {
        return img == null ? new Dimension(200, 200) : new Dimension(img.getWidth(), img.getHeight());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            int x = (getWidth() - img.getWidth()) / 2;
            int y = (getHeight() - img.getHeight()) / 2;
            g2d.drawImage(img, x, y, this);
            g2d.dispose();
        }
    }

    @Override
    public void setDelegate(ImageViewDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public ImageViewDelegate getDelegate() {
        return delegate;
    }

    @Override
    public JComponent getView() {
        return this;
    }

    @Override
    public void loadImageAt(int index) {
        ImageViewDelegate delegate = getDelegate();
        if (delegate != null) {
            img = null;
            Image file = delegate.imageAt(this, index);
            if (file != null) {
                // Create a BufferedImage with the same dimensions as the Image
                img = new BufferedImage(file.getWidth(null), file.getHeight(null), BufferedImage.TYPE_INT_ARGB);

                // Draw the Image onto the BufferedImage
                Graphics2D g2d = img.createGraphics();
                g2d.drawImage(file, 0, 0, null);
                g2d.dispose();
            }
            revalidate();
            repaint();
        }
    }

}