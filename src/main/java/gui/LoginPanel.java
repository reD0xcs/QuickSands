package gui;

import Components.RButton;
import Components.TextPrompt;
import DataBase.FireBaseService;
import DataBase.User;
import Stripe.StripeConfig;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPanel extends BasePanel {
    public LoginPanel(BaseFrame baseFrame) {
        setSize(baseFrame.getWidth(), baseFrame.getHeight());
        setLayout(new BorderLayout());

        // Create layered pane
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setSize(baseFrame.getWidth(), baseFrame.getHeight());

        // Add background image panel
        ImagePanel backgroundPanel = new ImagePanel("src/main/resources/design resources/login page.png");
        backgroundPanel.setSize(baseFrame.getWidth(), baseFrame.getHeight());
        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        // Create a panel for components
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

    @Override
    public void addComponents(BaseFrame baseFrame) {}

    public void addComponents(BaseFrame baseFrame, JPanel componentsPanel) {
        // Cursor
        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        // Email
        JTextField emailField = new JTextField();
        TextPrompt emailPrompt = new TextPrompt("Email:", emailField);
        emailPrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        emailPrompt.setForeground(Color.decode("#AAAAAA"));
        emailField.setBounds(50, 250, baseFrame.getWidth() / 3, 40);
        emailField.setFont(new Font("Dialog", Font.PLAIN, 23));
        componentsPanel.add(emailField);

        // Password
        JPasswordField passwordField = new JPasswordField("");
        TextPrompt passwordPrompt = new TextPrompt("Password:", passwordField);
        passwordPrompt.setFont(new Font("Dialog", Font.ITALIC, 23));
        passwordPrompt.setForeground(Color.decode("#AAAAAA"));
        passwordField.setBounds(50, 315, baseFrame.getWidth() / 3, 40);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 27));
        componentsPanel.add(passwordField);

        // Buttons
        RButton loginButton = new RButton("Login", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        loginButton.setBounds(50 + 109, 400, 210, 45);
        loginButton.setCursor(cursor);
        loginButton.setFont(new Font("Dialog", Font.BOLD, 23));
        loginButton.setForeground(Color.decode("#D9D9D9"));
        loginButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = String.valueOf(passwordField.getPassword());

            User user;
            user = FireBaseService.verifyAccount(email, password);
            if (user != null) {
                if (user.getRole().equals("user")) {

                    try {
                        Customer customer = StripeConfig.createStripeCustomer(user);
                        FireBaseService.saveCustomerToFirebase(user.getEmail(), customer.getId());
                    } catch (StripeException ex) {
                        throw new RuntimeException(ex);
                    }
                    baseFrame.changePanel(new ProfilePanel(baseFrame, user));
                } else {
                    baseFrame.changePanel(new AdminPanel(baseFrame, user));
                }
            }
            else{
                JOptionPane.showMessageDialog(this, "There is no account that matches the email and password you entered.", "Error", JOptionPane.ERROR_MESSAGE);

            }
        });
        componentsPanel.add(loginButton);

        JLabel registerLabel = new JLabel("<html><a href=\"#\">Don't have an account? Register Here</a></html>");
        registerLabel.setBounds(50, 460, baseFrame.getWidth() / 3, 30);
        registerLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerLabel.setCursor(cursor);
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                baseFrame.changePanel(new RegisterPanel(baseFrame));
            }
        });
        componentsPanel.add(registerLabel);
    }
}
