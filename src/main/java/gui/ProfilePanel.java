package gui;

import Components.HotelOffer;
import Components.RButton;
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
    public static BaseFrame addReservationFrame = new BaseFrame(1400, 600);

    private void reservation(ActionEvent e, ArrayList<HotelOffer> hotelOffer, int index){
        AddReservation addReservation = new AddReservation(addReservationFrame, hotelOffer, index);
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
        ArrayList<HotelOffer> hotelOffers = FireBaseService.loadAllOffers();
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

        // Wrap offersPanel in a container to center it
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel offersPanel = new JPanel();
        offersPanel.setLayout(new BoxLayout(offersPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < hotelOffers.size(); i++) {
            JPanel offerPanel = createOfferPanel(hotelOffers.get(i), i, hotelOffers); // Pass index i
            offerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            offersPanel.add(offerPanel);
            offersPanel.add(Box.createVerticalStrut(10)); // Add space between offers
        }

        containerPanel.add(offersPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(containerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createOfferPanel(HotelOffer offer, int index, ArrayList<HotelOffer> hotelOffers) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.setPreferredSize(new Dimension(700, 300)); // Set a preferred size for the panel

        JLabel hotelNameLabel = new JLabel(offer.getHotelName());
        hotelNameLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        hotelNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel hotelPriceLabel = new JLabel("Price: $" + offer.getHotelprice());
        hotelPriceLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        hotelPriceLabel.setHorizontalAlignment(SwingConstants.LEFT);

        ImageIcon imageIcon = null;
        if (!offer.getHotelImages().isEmpty()) {
            Image image = offer.getHotelImages().get(0);
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
                System.out.println("Clicked on Book Now for offer #" + (index + 1));
                reservation(e, hotelOffers, index);
            }
        });

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(hotelNameLabel);
        infoPanel.add(hotelPriceLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(bookButton);

        panel.add(imageLabel, BorderLayout.EAST);
        panel.add(infoPanel, BorderLayout.WEST);

        return panel;
    }
}
