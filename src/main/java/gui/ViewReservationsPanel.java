package gui;

import Components.*;
import DataBase.FireBaseService;
import DataBase.User;
import com.google.zxing.WriterException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class ViewReservationsPanel extends BasePanel{
    private final User user;

    public ViewReservationsPanel(BaseFrame baseFrame, User u){
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
        ArrayList<Reservation> reservations = FireBaseService.loadReservations(user);
        ArrayList<RoomOffer> roomOffers = FireBaseService.loadAllOffers();
        Collections.sort(roomOffers, new Comparator<RoomOffer>() {
            @Override
            public int compare(RoomOffer o1, RoomOffer o2) {
                return Long.compare(o1.getRoomId(), o2.getRoomId());
            }
        });
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
            //frame.changePanel(new ProfilePanel(frame, user));
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

        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Menu panel
        JPanel menuPanel = createMenuPanel(frame);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 1;
        containerPanel.add(menuPanel, gbc);

        // Offers panel
        JPanel offersPanel = new JPanel();
        offersPanel.setLayout(new BoxLayout(offersPanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < reservations.size(); i++) {
            JPanel offerPanel = createOfferPanel(reservations.get(i), i, reservations, roomOffers);
            offerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            offersPanel.add(offerPanel);
            offersPanel.add(Box.createVerticalStrut(10));
        }
        JScrollPane offersScrollPane = new JScrollPane(offersPanel);
        offersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.weighty = 1;
        containerPanel.add(offersScrollPane, gbc);

        add(containerPanel, BorderLayout.CENTER);
    }

    private JPanel createOfferPanel(Reservation reservation, int index, ArrayList<Reservation> roomOffers, ArrayList<RoomOffer> offers) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        panel.setPreferredSize(new Dimension(700, 300)); // Set a preferred size for the panel

        JLabel roomTypeLabel = new JLabel(offers.get(reservation.getRoomIndex() - 1).getRoomType());
        roomTypeLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        roomTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel roomPriceLabel = new JLabel("Price: $" + offers.get(reservation.getRoomIndex() - 1).getRoomPricePerNight());
        roomPriceLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
        roomPriceLabel.setHorizontalAlignment(SwingConstants.LEFT);

        ImageIcon imageIcon = null;
        if (!offers.get(reservation.getRoomIndex() - 1).getRoomImages().isEmpty()) {
            Image image = offers.get(reservation.getRoomIndex() - 1).getRoomImages().get(0);
            Image scaledImage = image.getScaledInstance(500, 500, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(scaledImage);
        }
        JLabel imageLabel = new JLabel(imageIcon);
        imageLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        RButton cancelButton = new RButton("Cancel", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        cancelButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(100, 40));

        // Add action listener to the book button to handle the action
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Clicked on Book Now for offer #" + (index + 1));
                try {
                    FireBaseService.cancelReservation(reservation.getQRname());
                } catch (ExecutionException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        RButton resendButton = new RButton("Resend details", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        resendButton.setFont(new Font("Dialog", Font.PLAIN, 16));
        resendButton.setForeground(Color.WHITE);
        resendButton.setPreferredSize(new Dimension(100, 40));

        // Add action listener to the book button to handle the action
        resendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Clicked on Book Now for offer #" + (index + 1));
                try {
                    BufferedImage QR = (BufferedImage) FireBaseService.loadQR(reservation.getQRname());
                    byte[] PDF = FireBaseService.loadPDF(reservation.getReceiptName());
                    EmailSender.sendEmail(user, QR, PDF);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (WriterException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(roomTypeLabel);
        infoPanel.add(roomPriceLabel);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(cancelButton);
        infoPanel.add(Box.createVerticalStrut(20));
        infoPanel.add(resendButton);

        panel.add(imageLabel, BorderLayout.EAST);
        panel.add(infoPanel, BorderLayout.WEST);

        return panel;
    }
    private JPanel createMenuPanel(BaseFrame baseFrame) {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 0, 5, 0); // Spacing between buttons

        SQButton homeButton = new SQButton("Home", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        homeButton.setFont(new Font("Dialog", Font.BOLD, 23));
        homeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        homeButton.setForeground(Color.WHITE);

        homeButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        homeButton.setMinimumSize(new Dimension(450, 50));
        homeButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        homeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#555555"));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#7A4641"));
            }
        });
        homeButton.addActionListener(e -> {
            //baseFrame.changePanel(new ProfilePanel(baseFrame, user));
        });
        menuPanel.add(homeButton, gbc);

        SQButton reservationsButton  = new SQButton("Your Reservations", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        reservationsButton.setFont(new Font("Dialog", Font.BOLD, 23));
        reservationsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reservationsButton.setForeground(Color.WHITE);

        reservationsButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        reservationsButton.setMinimumSize(new Dimension(450, 50));
        reservationsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        reservationsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#555555"));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#7A4641"));
            }
        });
        menuPanel.add(reservationsButton, gbc);

        SQButton settingsButton  = new SQButton("Settings", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        settingsButton.setFont(new Font("Dialog", Font.BOLD, 23));
        settingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsButton.setForeground(Color.WHITE);

        settingsButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        settingsButton.setMinimumSize(new Dimension(450, 50));
        settingsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        settingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#555555"));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#7A4641"));
            }
        });
        menuPanel.add(settingsButton, gbc);

        SQButton logoutButton  = new SQButton("Log Out", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        logoutButton.setFont(new Font("Dialog", Font.BOLD, 23));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setForeground(Color.WHITE);

        logoutButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 50));
        logoutButton.setMinimumSize(new Dimension(450, 50));
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#555555"));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                homeButton.setBackground(Color.decode("#7A4641"));
            }
        });

        logoutButton.addActionListener(e -> {
            baseFrame.changePanel(new LoginPanel(baseFrame));
        });
        menuPanel.add(logoutButton, gbc);
        return menuPanel;
    }
}
