package gui;

import Components.RButton;
import Components.TextPrompt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPanel extends BasePanel{
    public LoginPanel(BaseFrame baseFrame){
        setSize(baseFrame.getWidth(), baseFrame.getHeight());
        setLayout(null);
        setBackground(Color.decode("#F2F2F2"));
        addComponents(baseFrame);
    }
    @Override
    public void addComponents(BaseFrame baseFrame) {
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setBounds(0, 20, getWidth(), 60);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 50));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        //Cursor
        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        //Email
        JTextField emailField = new JTextField();
        TextPrompt emailPrompt = new TextPrompt("Email:", emailField);
        emailPrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        emailPrompt.setForeground(Color.decode("#AAAAAA"));
        emailField.setBounds(80, 200, getWidth() / 3, 40);
        emailField.setFont(new Font("Dialog", Font.PLAIN, 23));
        add(emailField);

        //Password
        JPasswordField passwordField = new JPasswordField("");
        TextPrompt passwordPrompt = new TextPrompt("Password:", passwordField);
        passwordPrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        passwordPrompt.setForeground(Color.decode("#AAAAAA"));
        passwordField.setBounds(80, 270, getWidth() / 3, 40);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 27));
        add(passwordField);

        //Buttons
        RButton loginButton = new RButton("Login", Color.WHITE, Color.decode("#00B7F0"), Color.decode("#AAAAAA"));
        loginButton.setBounds(80 + 109, 350, 210, 45);
        loginButton.setCursor(cursor);
        loginButton.setFont(new Font("Dialog", Font.PLAIN, 23));
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = String.valueOf(passwordField.getPassword());

           /*
           Logic for database connection
            */
        });
        add(loginButton);

        JLabel registerLabel = new JLabel("<html><a href=\"#\">Don't have an account? Register Here</a></html>");
        registerLabel.setBounds(80, 420, getWidth() / 3, 30);
        registerLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerLabel.setCursor(cursor);
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                baseFrame.changePanel(new RegisterPanel(baseFrame));



            }
        });
        add(registerLabel);
    }
}
