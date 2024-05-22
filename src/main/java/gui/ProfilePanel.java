package gui;

import Components.HotelOffer;
import Components.RButton;
import Components.RoomOffer;
import Components.SQButton;
import DataBase.FireBaseService;
import DataBase.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Objects;

public class ProfilePanel extends BasePanel {
    private final User user;

    private void reservation(ActionEvent e, ArrayList<RoomOffer> roomOffers, int index){
        BaseFrame addReservationFrame = new BaseFrame(1400, 600);
        AddReservation addReservation = new AddReservation(addReservationFrame, roomOffers.get(index), index);
        addReservationFrame.add(addReservation);
        addReservationFrame.setLocationRelativeTo(null);
        addReservationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addReservationFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                addReservationFrame.dispose();
            }
        });
        addReservationFrame.setVisible(true);

    }

    public ProfilePanel(BaseFrame baseFrame, User u) {
        user = u;
        setSize(baseFrame.getWidth(), baseFrame.getHeight());
        setLayout(null);
        setBackground(Color.decode("#F2F2F2"));
        addComponents(baseFrame);
    }

    @Override
    public void addComponents(BaseFrame baseFrame, JPanel componentsPanel) {
        // Not used, but needs to be implemented due to abstract method in BasePanel
    }

    @Override
    public void addComponents(BaseFrame frame) {
        ArrayList<RoomOffer> roomOffers = FireBaseService.loadAllOffers();
        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);
        setLayout(new BorderLayout());

        ImageIcon profile = new ImageIcon(Objects.requireNonNull(getClass().getResource("/Profile.png")));
        Image newProfile = profile.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        profile = new ImageIcon(newProfile);

        JButton profileButton = new JButton();
        profileButton.setBounds(5, 5, profile.getIconWidth(), profile.getIconHeight());
        profileButton.setCursor(cursor);
        profileButton.setIcon(profile);
        profileButton.setFocusPainted(false);
        profileButton.setContentAreaFilled(false);
        profileButton.setBorderPainted(false);
        profileButton.addActionListener(e -> {
            frame.changePanel(new ProfilePanel(frame, user));
        });
        add(profileButton, BorderLayout.NORTH);

        String welcomeMessage = "<html><body style='text-align:center'><b>Hello, " + user.getFirst_name() + " " + user.getLast_name() + "</b></body></html>";
        JLabel welcomeMessageLabel = new JLabel(welcomeMessage);
        welcomeMessageLabel.setBounds(80, 20, getWidth() - 10, 40);
        welcomeMessageLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        welcomeMessageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(welcomeMessageLabel, BorderLayout.NORTH);

        // Title
        JLabel travelAppLabel = new JLabel("BookNgo");
        travelAppLabel.setBounds(0, 20, super.getWidth(), 60);
        travelAppLabel.setFont(new Font("Dialog", Font.BOLD, 50));
        travelAppLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(travelAppLabel, BorderLayout.NORTH);

        // Container panel with GridBagLayout
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Menu panel
        JPanel menuPanel = createMenuPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 1;
        containerPanel.add(menuPanel, gbc);

        // Offers panel
        JPanel offersPanel = new JPanel();
        offersPanel.setLayout(new BoxLayout(offersPanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < roomOffers.size(); i++) {
            JPanel offerPanel = createOfferPanel(roomOffers.get(i), i, roomOffers);
            offerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            offersPanel.add(offerPanel);
            offersPanel.add(Box.createVerticalStrut(10));
        }

        // Add the offers panel to a JScrollPane
        JScrollPane offersScrollPane = new JScrollPane(offersPanel);
        offersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.weighty = 1;
        containerPanel.add(offersScrollPane, gbc);

        add(containerPanel, BorderLayout.CENTER);
    }


    private JPanel createOfferPanel(RoomOffer offer, int index, ArrayList<RoomOffer> roomOffers) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.setPreferredSize(new Dimension(700, 300)); // Set a preferred size for the panel

        JLabel roomTypeLabel = new JLabel(offer.getRoomType());
        roomTypeLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        roomTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel roomPriceLabel = new JLabel("Price: $" + offer.getRoomPricePerNight());
        roomPriceLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        roomPriceLabel.setHorizontalAlignment(SwingConstants.LEFT);

        ImageIcon imageIcon = null;
        if (!offer.getRoomImages().isEmpty()) {
            Image image = offer.getRoomImages().get(0);
            Image scaledImage = image.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);
        }
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        RButton bookButton = new RButton("Book now", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        bookButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        bookButton.setForeground(Color.WHITE);
        bookButton.setPreferredSize(new Dimension(100, 40));

        // Add action listener to the book button to handle the action
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Clicked on Book Now for offer #" + (index + 1));
                reservation(e, roomOffers, index);
            }
        });

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(roomTypeLabel);
        infoPanel.add(roomPriceLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(bookButton);

        panel.add(imageLabel, BorderLayout.EAST);
        panel.add(infoPanel, BorderLayout.WEST);

        return panel;
    }
    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridBagLayout());
        menuPanel.setBackground(Color.decode("#333333")); // Dark background color

        String[] buttonLabels = {"Home", "Profile", "Reservations", "Settings", "Logout"};
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0); // Spacing between buttons

        for (String label : buttonLabels) {
            SQButton button = new SQButton(label, Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
            button.setFont(new Font("Dialog", Font.PLAIN, 18)); // Larger font size
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Set button size to match the menu panel width
            button.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
            button.setMinimumSize(new Dimension(450, 50));
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            // Add hover effect
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(Color.decode("#555555"));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(Color.decode("#7A4641"));
                }
            });

            menuPanel.add(button, gbc);
        }

        return menuPanel;
    }

}