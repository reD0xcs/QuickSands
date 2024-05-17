import DataBase.FireBaseService;

import gui.BaseFrame;

import javax.swing.*;
import java.io.IOException;


public class AppLauncher {
    public static void main(String[] args) throws IOException {

        FireBaseService.getInstance();

        SwingUtilities.invokeLater(() -> {
            BaseFrame baseFrame = new BaseFrame();
            baseFrame.setVisible(true);
        });

    }
}
