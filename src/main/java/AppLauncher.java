import gui.BaseFrame;

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BaseFrame baseFrame = new BaseFrame();
            baseFrame.setVisible(true);
        });
    }
}
