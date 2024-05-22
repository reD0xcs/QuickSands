package gui;

import Components.HotelOffer;
import Components.RButton;
import Components.RoomOffer;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class AddReservation extends JPanel {
    private DefaultImageController imageController = new DefaultImageController(new DefaultImageViewPane(), new DefaultImageModel(new ArrayList<>()));
    private DefaultNavigationController navigationController = new DefaultNavigationController(new DefaultNavigationView(), new DefaultNavigationModel(0, 4));
    private java.util.Date firstDateSelected = null;
    private java.util.Date secondDateSelected = null;
    public AddReservation(BaseFrame baseFrame, RoomOffer offer, int index) {
        setSize(1400, 600);
        setLayout(null);

        // Clear previous components if any

        JLabel title = new JLabel("Add Reservation");
        title.setBounds(0, 20, 600, 50);
        title.setFont(new Font("Segoe UI", Font.BOLD, 35));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);


        UtilDateModel model1 = new UtilDateModel();
        Properties p1 = new Properties();
        p1.put("text.today", "Today");
        p1.put("text.month", "Month");
        p1.put("text.yead", "Year");
        JDatePanelImpl firstDate = new JDatePanelImpl(model1, p1);
        firstDate.setBounds(30, 120, 200, 200);
        firstDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                firstDateSelected = model1.getValue();
            }
        });
        add(firstDate);

        UtilDateModel model2 = new UtilDateModel();
        Properties p2 = new Properties();
        p2.put("text.today", "Today");
        p2.put("text.month", "Month");
        p2.put("text.year", "Year");
        JDatePanelImpl secondDate = new JDatePanelImpl(model2, p2);
        secondDate.setBounds(270, 120, 200, 200);
        secondDate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondDateSelected = model2.getValue();
            }
        });
        add(secondDate);

        Image[] resizedImages = new Image[offer.getRoomImages().size()];
        for (int i = 0; i < offer.getRoomImages().size(); i++) {
            resizedImages[i] = offer.getRoomImages().get(i).getScaledInstance(800, 450, Image.SCALE_SMOOTH);
        }

        // Create image view and controller
        ImageView imageView = new DefaultImageViewPane();
        imageController = new DefaultImageController(imageView, new DefaultImageModel(Arrays.asList(resizedImages)));
        JScrollPane imageScrollPane = new JScrollPane(imageController.getView().getView());
        imageScrollPane.setBounds(540, 40, 810, 460);
        add(imageScrollPane);
        // Create navigation controller
        NavigationView navView = new DefaultNavigationView();
        NavigationModel navModel = new DefaultNavigationModel(0, 0);
        navigationController = new DefaultNavigationController(navView, navModel);
        navigationController.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                NavigationController controller = (NavigationController) e.getSource();
                imageController.loadImageAt(controller.getModel().getIndex());
            }
        });

        // Add image scroll pane and navigation panel to this panel

        navigationController.setModel(new DefaultNavigationModel(0, resizedImages.length));
        JPanel navPanel = (JPanel) navigationController.getView().getView();
        navPanel.setPreferredSize(new Dimension(100, getHeight()));
        navPanel.setBounds(750, 480, 300, getHeight() / 6);
        add(navPanel);

        // Add button to close window and reset image controller
        RButton addButton = new RButton("Book", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        addButton.setCursor(cursor);
        addButton.setFont(new Font("Dialog", Font.PLAIN, 23));
        addButton.setBounds(30, 420, 220, 70);
        addButton.setForeground(Color.decode("#D9D9D9"));
        addButton.addActionListener(e -> {
            imageController.setModel(new DefaultImageModel(new ArrayList<>()));
            baseFrame.dispose(); // Close the current window
            // Reset image controller for reuse
            //Need to make this functional
        });
        add(addButton);
    }
}