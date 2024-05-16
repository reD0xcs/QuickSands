package gui;

import javax.swing.*;

abstract public class BasePanel extends JPanel {
    protected int WIDTH = 1280;
    protected int HEIGHT = 720;

    public abstract void addComponents(BaseFrame baseFrame, JPanel componentsPanel);

    public abstract void addComponents(BaseFrame frame);
}
