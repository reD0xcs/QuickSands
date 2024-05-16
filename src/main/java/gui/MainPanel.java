package gui;

import Components.*;
import javax.swing.*;
import java.awt.*;

public class MainPanel extends BasePanel {
    public MainPanel(BaseFrame baseFrame) {
        setSize(baseFrame.getWidth(), baseFrame.getHeight());
        setLayout(new BorderLayout());

        // Create layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setSize(baseFrame.getWidth(), baseFrame.getHeight());

        // Add background image panel
        ImagePanel backgroundPanel = new ImagePanel("C:\\Users\\catal\\OneDrive\\Documents\\GitHub\\bookNgo\\src\\main\\resources\\design resources\\default page.png");
        backgroundPanel.setSize(baseFrame.getWidth(), baseFrame.getHeight());
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        // Create a panel for buttons and labels
        JPanel componentsPanel = new JPanel();
        componentsPanel.setOpaque(false); // Make it transparent
        componentsPanel.setLayout(null);
        componentsPanel.setSize(baseFrame.getWidth(), baseFrame.getHeight());

        // Add components
        addComponents(baseFrame, componentsPanel);

        // Add components panel to layered pane
        layeredPane.add(componentsPanel, JLayeredPane.PALETTE_LAYER);

        // Add layered pane to main panel
        add(layeredPane, BorderLayout.CENTER);
    }

    public void addComponents(BaseFrame baseFrame, JPanel componentsPanel) {
        // Cursor
        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        // Login Button
        RButton loginButton = new RButton("Login", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        loginButton.setBounds(60, 200, 400, 100);
        loginButton.setCursor(cursor);
        loginButton.setFont(new Font("Dialog", Font.PLAIN, 50));
        loginButton.addActionListener(e -> {
            baseFrame.changePanel(new LoginPanel(baseFrame));
        });
        componentsPanel.add(loginButton);

        // Register Button
        RButton registerButton = new RButton("Register", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        registerButton.setBounds(60, 350, 400, 100);
        registerButton.setCursor(cursor);
        registerButton.setFont(new Font("Dialog", Font.PLAIN, 50));
        registerButton.addActionListener(e -> {
            baseFrame.changePanel(new RegisterPanel(baseFrame));
        });
        componentsPanel.add(registerButton);

        // Exit Button
        RButton exitButton = new RButton("Exit", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        exitButton.setBounds(60, 500, 400, 100);
        exitButton.setCursor(cursor);
        exitButton.setFont(new Font("Dialog", Font.PLAIN, 50));
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
        componentsPanel.add(exitButton);
    }

    @Override
    public void addComponents(BaseFrame frame) {

    }
}
