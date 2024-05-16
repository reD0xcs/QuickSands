package gui;

import Components.RButton;
import DataBase.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class AdminPanel extends BasePanel{

    public static BaseFrame addOfferFrame = new BaseFrame(1400, 600);
    private final User user;

    private void offer(ActionEvent e) {
        AddOffer addOffer = new AddOffer(addOfferFrame);
        addOfferFrame.add(addOffer);
        addOfferFrame.setLocationRelativeTo(null);
        addOfferFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addOfferFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                addOfferFrame.dispose();
            }
        });
        addOfferFrame.setVisible(true);
    }

    public AdminPanel(BaseFrame baseFrame, User u){
        user = u;
        setSize(baseFrame.getWidth(), baseFrame.getHeight());
        setLayout(null);
        setBackground(Color.decode("#F2F2F2"));
        addComponents(baseFrame);
    }

    @Override
    public void addComponents(BaseFrame baseFrame, JPanel componentsPanel) {

    }

    @Override
    public void addComponents(BaseFrame frame) {
        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);
        ImageIcon profile = new ImageIcon(Objects.requireNonNull(getClass().getResource("/adminProfile.png")));
        Image newProfile = profile.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        profile = new ImageIcon(newProfile);


        JButton profileButton = new JButton();
        profileButton.setBounds(10, 10, profile.getIconWidth(), profile.getIconHeight());
        profileButton.setCursor(cursor);
        profileButton.setIcon(profile);
        profileButton.setFocusPainted(false);
        profileButton.setContentAreaFilled(false);
        profileButton.setBorderPainted(false);
        add(profileButton);

        //Welcome text
        String welcomeMessage = "<html>" +
                "<body style='text-align:center'>" +
                "<b>Hello, " + user.getFirst_name() + " " + user.getLast_name() +
                "</b>" + "</body></html>";
        JLabel welcomeMessageLabel = new JLabel(welcomeMessage);
        welcomeMessageLabel.setBounds(80, 20, getWidth() - 10, 40);
        welcomeMessageLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        welcomeMessageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(welcomeMessageLabel);

        //Title
        JLabel adminTitle = new JLabel("Admin Panel");
        adminTitle.setBounds(0, 20, super.getWidth(), 60);
        adminTitle.setFont(new Font("Dialog", Font.BOLD, 50));
        adminTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(adminTitle);

        //Buttons
        RButton userDataButton = new RButton("User Data", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        userDataButton.setBounds(100, 170, 430, 100);
        userDataButton.setCursor(cursor);
        userDataButton.setFont(new Font("Dialog", Font.PLAIN, 40));
        userDataButton.setForeground(Color.decode("#D9D9D9"));
        userDataButton.addActionListener(e->{
            //Set the User Data scene;
        });
        add(userDataButton);

        RButton addNewOfferButton = new RButton("Add Accomodation", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        addNewOfferButton.setBounds(100, 300, 430, 100);
        addNewOfferButton.setCursor(cursor);
        addNewOfferButton.setFont(new Font("Dialog", Font.PLAIN, 40));
        addNewOfferButton.setForeground(Color.decode("#D9D9D9"));
        addNewOfferButton.addActionListener(e->{
            offer(e);
        });
        add(addNewOfferButton);

        RButton logOffButton = new RButton("LogOut", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        logOffButton.setBounds(100, 430, 430, 100);
        logOffButton.setCursor(cursor);
        logOffButton.setFont(new Font("Dialog", Font.PLAIN, 40));
        logOffButton.setForeground(Color.decode("#D9D9D9"));
        logOffButton.addActionListener(e->{
            frame.changePanel(new LoginPanel(frame));
        });
        add(logOffButton);
    }
}
