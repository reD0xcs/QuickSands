package gui;

import javax.swing.*;

public class BaseFrame extends JFrame {
    private JPanel currentPanel;

    public BaseFrame() {
        initialize();
    }
    public BaseFrame(int w, int h){
        setTitle("BookNgo");

        setSize(w, h);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setResizable(false);

        setLocationRelativeTo(null);
    }
    private void initialize() {
        setTitle("BookNgo");

        setSize(1280, 720);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setResizable(false);

        setLocationRelativeTo(null);

        changePanel(new MainPanel(this));
    }

    public void changePanel(JPanel panel){
        if(currentPanel != null){
            getContentPane().remove(currentPanel);
        }
        currentPanel = panel;
        add(currentPanel);

        revalidate();
        repaint();
    }
}
