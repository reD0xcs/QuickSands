package gui;

import Components.RButton;
import Components.TextPrompt;
import DataBase.FireBaseService;
import DataBase.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegisterPanel extends BasePanel{

    public RegisterPanel(BaseFrame baseFrame){
        setSize(baseFrame.getWidth(), baseFrame.getHeight());
        setLayout(null);
        setBackground(Color.decode("#F2F2F2"));
        addComponents(baseFrame);
    }
    @Override
    public void addComponents(BaseFrame baseframe) {
        JLabel titleLabel = new JLabel("BookNgo");
        titleLabel.setBounds(0, 20, getWidth(), 60);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 50));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel);

        JTextField fnameField = new JTextField();
        TextPrompt fnamePrompt = new TextPrompt("First Name:", fnameField);
        fnamePrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        fnamePrompt.setForeground(Color.decode("#AAAAAA"));
        fnameField.setBounds(80, 170, getWidth() / 3, 40);
        fnameField.setFont(new Font("Dialog", Font.PLAIN, 23));
        add(fnameField);

        JTextField lnameField = new JTextField();
        TextPrompt lnamePrompt = new TextPrompt("Last Name:", lnameField);
        lnamePrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        lnamePrompt.setForeground(Color.decode("#AAAAAA"));
        lnameField.setBounds(80, 240, getWidth() / 3, 40);
        lnameField.setFont(new Font("Dialog", Font.PLAIN, 23));
        add(lnameField);

        JTextField emailField = new JTextField();
        TextPrompt emailPrompt = new TextPrompt("Email:", emailField);
        emailPrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        emailPrompt.setForeground(Color.decode("#AAAAAA"));
        emailField.setBounds(80, 310, getWidth() / 3, 40);
        emailField.setFont(new Font("Dialog", Font.PLAIN, 23));
        add(emailField);

        JPasswordField passwordField = new JPasswordField();
        TextPrompt passwordPrompt = new TextPrompt("Password:", passwordField);
        passwordPrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        passwordPrompt.setForeground(Color.decode("#AAAAAA"));
        passwordField.setBounds(80, 380, getWidth() / 3, 40);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 23));
        add(passwordField);

        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        RButton registerButton = new RButton("Register", Color.WHITE, Color.decode("#00B7F0"), Color.decode("#AAAAAA"));
        registerButton.setFont(new Font("Dialog", Font.PLAIN, 23));
        registerButton.setBounds(80 + 109, 450, 210, 45);
        registerButton.setCursor(cursor);
        registerButton.addActionListener(e -> {
            String first_name = fnameField.getText();
            String last_name = lnameField.getText();
            String email = emailField.getText();
            String password = String.valueOf(passwordField.getPassword());

            int validate = validateUserInput(first_name, last_name, email, password);
            switch(validate){
                case 0:
                   String role = "user";
                   User user = null;
                    user = FireBaseService.registerUser(first_name, last_name, email, password, role);
                    if(user != null){
                       baseframe.changePanel(new ProfilePanel(baseframe, user));
                   }
                case 1:
                    JOptionPane.showMessageDialog(baseframe, "Error: Every field should be completed");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(baseframe, "Error: The password should be at least 8 characters long and at most 20 characters. \n " +
                            "From which: at least one digit \n" +
                            "at least one upper case letter \n" +
                            "at least one lower case letter \n" +
                            "at least one special character \n" +
                            "it doesnt contain any white spaces");
                    break;
                case 3:
                    JOptionPane.showMessageDialog(baseframe, "Error: The email is not valid");
                    break;
                case 4:
                    JOptionPane.showMessageDialog(baseframe, "Error: the first name should only contain letters");
                    break;
                case 5:
                    JOptionPane.showMessageDialog(baseframe, "Error: the last name should only contain letters");
                    break;
            }
        });
        add(registerButton);

        JLabel loginLabel = new JLabel("<html><a href=\"#\">Already have an account? Login Here</a></html>");
        loginLabel.setBounds(70, 520, getWidth() / 3, 30);
        loginLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.setCursor(cursor);
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Switch back to the LoginPanel
                baseframe.changePanel(new LoginPanel(baseframe));
            }
        });
        add(loginLabel);

    }
    private int validateUserInput(String first_name, String last_name, String email, String password){
        if(first_name.isEmpty() || last_name.isEmpty() || email.isEmpty() || password.isEmpty()){
            return 1;
        }
        if(!validatePassword(password)){
            return 2;
        }
        if(!validateEmail(email)){
            return 3;
        }
        if(validateName(first_name)){
            return 4;
        }
        if(validateName(last_name)){
            return 5;
        }
        return 0;
    }
    public boolean validatePassword(String password){
        String regexPassword = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        Pattern p = Pattern.compile(regexPassword);

        Matcher m = p.matcher(password);

        return m.matches();
    }
    public boolean validateEmail(String email){
        String regexEmail = "^(.+)@(\\S+)$";
        Pattern p = Pattern.compile(regexEmail);
        Matcher m = p.matcher(email);

        return m.matches();
    }
    public boolean validateName(String name){
        return name.matches(".*\\d.*");
    }
}
