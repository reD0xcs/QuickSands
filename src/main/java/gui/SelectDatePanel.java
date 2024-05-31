package gui;

import Components.RButton;
import Components.RoomOffer;
import Components.SQButton;
import DataBase.FireBaseService;
import DataBase.User;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class SelectDatePanel extends BasePanel{
    private final User user;
    private java.util.Date firstDateSelected = null;
    private java.util.Date secondDateSelected = null;


    public SelectDatePanel(BaseFrame baseFrame, User u){
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
    public void addComponents(BaseFrame baseFrame) {
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
            //baseFrame.changePanel(new ProfilePanel(baseFrame, user));
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
        JPanel menuPanel = createMenuPanel(baseFrame);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        gbc.weighty = 1;
        containerPanel.add(menuPanel, gbc);

        // Date panel
        JPanel datePanel = new JPanel();
        datePanel.setLayout(null);

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date minDate = calendar.getTime();

        Font datePickerFont = new Font("Segoe UI", Font.PLAIN, 16);

        // Create first date picker
        UtilDateModel model1 = new UtilDateModel();
        Properties p1 = new Properties();
        p1.put("text.today", "Today");
        p1.put("text.month", "Month");
        p1.put("text.year", "Year");
        JDatePanelImpl firstDatePanel = new JDatePanelImpl(model1, p1);
        JDatePickerImpl firstDatePicker = new JDatePickerImpl(firstDatePanel, new DateComponentFormatter());
        firstDatePicker.setBounds(230, 120, 200, 30);
        firstDatePicker.getJFormattedTextField().setFont(datePickerFont);


        // Create second date picker
        UtilDateModel model2 = new UtilDateModel();
        Properties p2 = new Properties();
        p2.put("text.today", "Today");
        p2.put("text.month", "Month");
        p2.put("text.year", "Year");
        JDatePanelImpl secondDatePanel = new JDatePanelImpl(model2, p2);
        JDatePickerImpl secondDatePicker = new JDatePickerImpl(secondDatePanel, new DateComponentFormatter());
        secondDatePicker.setBounds(500, 120, 200, 30);
        secondDatePicker.getJFormattedTextField().setFont(datePickerFont);

        firstDatePicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                firstDateSelected = model1.getValue();
                firstDateSelected = stripTime(firstDateSelected);
                if (firstDateSelected != null && firstDateSelected.before(minDate)) {
                    model1.setValue(minDate);
                    firstDateSelected = minDate;
                }
                else if(firstDateSelected != null && secondDateSelected != null && secondDateSelected.equals(firstDateSelected)){
                    Date default1 = new Date(firstDateSelected.getTime() - 24 * 60 * 60 * 1000);
                    model1.setValue(default1);
                    firstDateSelected = default1;
                }
                else if(firstDateSelected != null && secondDateSelected != null && firstDateSelected.after(secondDateSelected)){
                    Date default1 = new Date(firstDateSelected.getTime() + 24 * 60 * 60 * 1000);
                    model2.setValue(default1);
                    secondDateSelected = default1;
                }
            }
        });
        datePanel.add(firstDatePicker);

        secondDatePicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(firstDateSelected != null){
                    secondDateSelected = model2.getValue();
                    secondDateSelected = stripTime(secondDateSelected);
                    if(secondDateSelected != null && (secondDateSelected.before(minDate))){
                        Date default2 = new Date(firstDateSelected.getTime() + 24 * 60 * 60 * 1000);
                        model2.setValue(default2);
                        secondDateSelected = default2;
                    }
                    else if(secondDateSelected != null && secondDateSelected.before(firstDateSelected)){
                        Date default2 = new Date(firstDateSelected.getTime() + 24 * 60 * 60 * 1000);
                        model2.setValue(default2);
                        secondDateSelected = default2;
                    }
                    else if(secondDateSelected != null && secondDateSelected.equals(firstDateSelected)){
                        Date default2 = new Date(firstDateSelected.getTime() +  24 * 60 * 60 * 1000);
                        model2.setValue(default2);
                        secondDateSelected = default2;
                    }
                }
            }
        });
        datePanel.add(secondDatePicker);

        // Create search button
        RButton searchButton = new RButton("Search", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        searchButton.setFont(new Font("Dialog", Font.BOLD, 23));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBounds(360, 300, 210, 45);
        searchButton.addActionListener(e -> {
            firstDateSelected = (java.util.Date) firstDatePicker.getModel().getValue();
            secondDateSelected = (java.util.Date) secondDatePicker.getModel().getValue();
            ArrayList<RoomOffer> rooms = FireBaseService.getAvailableRoomOffers(firstDateSelected, secondDateSelected);
            baseFrame.changePanel(new ProfilePanel(baseFrame, user, rooms));
        });

        datePanel.add(searchButton);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.weighty = 1;
        containerPanel.add(datePanel, gbc);

        add(containerPanel, BorderLayout.CENTER);
    }

    private Date stripTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
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
        reservationsButton.addActionListener(e -> {
            baseFrame.changePanel(new ViewReservationsPanel(baseFrame, user));
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
