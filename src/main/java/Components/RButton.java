package Components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RButton extends JButton {
    private Color bgColor;
    private Color borderColor;
    private Color hoverColor;
    private boolean hover;

    public RButton(String text, Color bgColor, Color borderColor, Color hoverColor) {
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
        g2.drawRoundRect(offset, offset, getWidth() - thickness, getHeight() - thickness, 90, 90);
        if (hover) {
            g2.setColor(hoverColor);
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, getHeight(), getHeight());
        } else {
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, getHeight(), getHeight());
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
        g2.drawRoundRect(offset, offset, getWidth() - thickness, getHeight() - thickness, getHeight(), getHeight());
        g2.dispose();
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(150, 50);
    }
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

}

