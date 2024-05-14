package gui;

import Components.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainPanel extends BasePanel {
    public MainPanel(BaseFrame baseFrame) {
        setSize(baseFrame.getWidth(), baseFrame.getHeight());
        setLayout(null);
        setBackground(Color.decode("#F2F2F2"));
        addComponents(baseFrame);
    }

    @Override
    public void addComponents(BaseFrame baseFrame) {
        //Title
        JLabel titleLabel = new JLabel("BookNgo");
        titleLabel.setBounds(0, 20, WIDTH, 60);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 50));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        //Cursor
        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        //Buttons
        RButton loginButton = new RButton("Login", Color.WHITE, Color.decode("#00B7F0"), Color.decode("#AAAAAA"));
        loginButton.setBounds(100, 260, 430, 100);
        loginButton.setCursor(cursor);
        loginButton.setFont(new Font("Dialog", Font.PLAIN, 50));
        loginButton.addActionListener(e -> {
            baseFrame.changePanel(new LoginPanel(baseFrame));
        });
        add(loginButton);

        RButton registerButton = new RButton("Register", Color.WHITE, Color.decode("#00B7F0"), Color.decode("#AAAAAA"));
        registerButton.setBounds(100, 390, 430, 100);
        registerButton.setCursor(cursor);
        registerButton.setFont(new Font("Dialog", Font.PLAIN, 50));
        registerButton.addActionListener(e ->{
            baseFrame.changePanel(new RegisterPanel(baseFrame));
        });
        add(registerButton);

        RButton exitButton = new RButton("Exit", Color.WHITE, Color.decode("#00B7F0"), Color.decode("#AAAAAA"));
        exitButton.setBounds(100, 520, 430, 100);
        exitButton.setCursor(cursor);
        exitButton.setFont(new Font("Dialog", Font.PLAIN, 50));
        add(exitButton);
    }
}
