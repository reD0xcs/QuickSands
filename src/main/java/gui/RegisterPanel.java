package gui;

import Components.RButton;
import Components.TextPrompt;
import DataBase.FireBaseService;
import DataBase.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegisterPanel extends BasePanel {

    public RegisterPanel(BaseFrame baseFrame) {
        setSize(baseFrame.getWidth(), baseFrame.getHeight());
        setLayout(new BorderLayout());

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setSize(baseFrame.getWidth(), baseFrame.getHeight());

        ImagePanel backgroundPanel = new ImagePanel("C:\\Users\\catal\\OneDrive\\Documents\\GitHub\\bookNgo\\src\\main\\resources\\design resources\\register.png");
        backgroundPanel.setSize(baseFrame.getWidth(), baseFrame.getHeight());
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        JPanel componentsPanel = new JPanel();
        componentsPanel.setOpaque(false);
        componentsPanel.setLayout(null);
        componentsPanel.setSize(baseFrame.getWidth(), baseFrame.getHeight());

        addComponents(baseFrame, componentsPanel);

        layeredPane.add(componentsPanel, JLayeredPane.PALETTE_LAYER);

        add(layeredPane, BorderLayout.CENTER);
    }

    @Override
    public void addComponents(BaseFrame baseFrame, JPanel componentsPanel) {
        JTextField fnameField = new JTextField();
        TextPrompt fnamePrompt = new TextPrompt("First Name:", fnameField);
        fnamePrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        fnamePrompt.setForeground(Color.decode("#AAAAAA"));
        fnameField.setBounds(50, 200, baseFrame.getWidth() / 3, 40);
        fnameField.setFont(new Font("Dialog", Font.PLAIN, 23));
        componentsPanel.add(fnameField);

        JTextField lnameField = new JTextField();
        TextPrompt lnamePrompt = new TextPrompt("Last Name:", lnameField);
        lnamePrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        lnamePrompt.setForeground(Color.decode("#AAAAAA"));
        lnameField.setBounds(50, 270, baseFrame.getWidth() / 3, 40);
        lnameField.setFont(new Font("Dialog", Font.PLAIN, 23));
        componentsPanel.add(lnameField);

        JTextField emailField = new JTextField();
        TextPrompt emailPrompt = new TextPrompt("Email:", emailField);
        emailPrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        emailPrompt.setForeground(Color.decode("#AAAAAA"));
        emailField.setBounds(50, 340, baseFrame.getWidth() / 3, 40);
        emailField.setFont(new Font("Dialog", Font.PLAIN, 23));
        componentsPanel.add(emailField);

        JPasswordField passwordField = new JPasswordField();
        TextPrompt passwordPrompt = new TextPrompt("Password:", passwordField);
        passwordPrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        passwordPrompt.setForeground(Color.decode("#AAAAAA"));
        passwordField.setBounds(50, 410, baseFrame.getWidth() / 3, 40);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 23));
        componentsPanel.add(passwordField);

        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        RButton registerButton = new RButton("Register", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        registerButton.setFont(new Font("Dialog", Font.PLAIN, 23));
        registerButton.setBounds(50 + 109, 480, 210, 45);
        registerButton.setCursor(cursor);
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> {
            String first_name = fnameField.getText();
            String last_name = lnameField.getText();
            String email = emailField.getText();
            String password = String.valueOf(passwordField.getPassword());

            int validate = validateUserInput(first_name, last_name, email, password);
            switch (validate) {
                case 0:
                    String role = "user";
                    User user = FireBaseService.registerUser(first_name, last_name, email, password, role);
                    if (user != null) {
                        baseFrame.changePanel(new ProfilePanel(baseFrame, user));
                    }
                    break;
                case 1:
                    JOptionPane.showMessageDialog(baseFrame, "Error: Every field should be completed");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(baseFrame, "Error: The password should be at least 8 characters long and at most 20 characters. \n " +
                            "From which: at least one digit \n" +
                            "at least one upper case letter \n" +
                            "at least one lower case letter \n" +
                            "at least one special character \n" +
                            "it doesn't contain any white spaces");
                    break;
                case 3:
                    JOptionPane.showMessageDialog(baseFrame, "Error: The email is not valid");
                    break;
                case 4:
                    JOptionPane.showMessageDialog(baseFrame, "Error: the first name should only contain letters");
                    break;
                case 5:
                    JOptionPane.showMessageDialog(baseFrame, "Error: the last name should only contain letters");
                    break;
            }
        });
        componentsPanel.add(registerButton);

        JLabel loginLabel = new JLabel("<html><a href=\"#\">Already have an account? Login Here</a></html>");
        loginLabel.setBounds(50, 550, baseFrame.getWidth() / 3, 30);
        loginLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.setCursor(cursor);
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                baseFrame.changePanel(new LoginPanel(baseFrame));
            }
        });
        componentsPanel.add(loginLabel);
    }

    private int validateUserInput(String first_name, String last_name, String email, String password) {
        if (first_name.isEmpty() || last_name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return 1;
        }
        if (!validatePassword(password)) {
            return 2;
        }
        if (!validateEmail(email)) {
            return 3;
        }
        if (validateName(first_name)) {
            return 4;
        }
        if (validateName(last_name)) {
            return 5;
        }
        return 0;
    }

    public boolean validatePassword(String password) {
        String regexPassword = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        Pattern p = Pattern.compile(regexPassword);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    public boolean validateEmail(String email) {
        String regexEmail = "^(.+)@(\\S+)$";
        Pattern p = Pattern.compile(regexEmail);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public boolean validateName(String name) {
        return !name.matches(".*\\d.*");
    }

    @Override
    public void addComponents(BaseFrame frame) {

    }
}
