package Components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SQButton extends JButton {
    private final Color bgColor;
    private final Color borderColor;
    private final Color hoverColor;
    private boolean hover;

    public SQButton(String text, Color bgColor, Color borderColor, Color hoverColor) {
        super(text);
        this.bgColor = bgColor;
        this.borderColor = borderColor;
        this.hoverColor = hoverColor;

        setUI(new BasicButtonUI());
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);

        Font buttonFont = getFont();
        setFont(buttonFont.deriveFont(buttonFont.getSize() * 3f));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int thickness = getWidth() / 2;
        int offset = thickness / 2;
        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(borderColor);
        g2.drawRect(offset, offset, getWidth() - thickness, getHeight() - thickness);
        if (hover) {
            g2.setColor(hoverColor);
            g2.fillRect(0, 0, getWidth() - 2, getHeight() - 2);
        } else {
            g2.setColor(bgColor);
            g2.fillRect(0, 0, getWidth() - 2, getHeight() - 2);
        }
        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int thickness = 4;
        int offset = thickness / 2;
        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(borderColor);
        g2.drawRect(offset, offset, getWidth() - thickness, getHeight() - thickness);
        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        int size = 100;  // Default size
        return new Dimension(size, size);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
    }

    @Override
    public void setPreferredSize(Dimension size) {
        super.setPreferredSize(new Dimension(size.width, size.height));
    }
}
