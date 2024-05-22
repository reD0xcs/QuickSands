package gui;

import Components.HotelOffer;
import Components.RButton;
import Components.RoomOffer;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AddReservation extends JPanel {
    private DefaultImageController imageController = new DefaultImageController(new DefaultImageViewPane(), new DefaultImageModel(new ArrayList<>()));
    private DefaultNavigationController navigationController = new DefaultNavigationController(new DefaultNavigationView(), new DefaultNavigationModel(0, 4));
    private java.util.Date firstDateSelected = null;
    private java.util.Date secondDateSelected = null;
    private JLabel priceLabel;

    public AddReservation(BaseFrame baseFrame, RoomOffer offer, int index) {
        setSize(1400, 600);
        setLayout(null);

        JLabel title = new JLabel("Add Reservation");
        title.setBounds(0, 20, 600, 50);
        title.setFont(new Font("Segoe UI", Font.BOLD, 35));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        // Get the current date
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date minDate = calendar.getTime();

        Font datePickerFont = new Font("Segoe UI", Font.PLAIN, 16);

        // First date picker
        UtilDateModel model1 = new UtilDateModel();
        Properties p1 = new Properties();
        p1.put("text.today", "Today");
        p1.put("text.month", "Month");
        p1.put("text.year", "Year");
        JDatePanelImpl firstDatePanel = new JDatePanelImpl(model1, p1);
        JDatePickerImpl firstDatePicker = new JDatePickerImpl(firstDatePanel, new DateComponentFormatter());
        firstDatePicker.setBounds(30, 120, 200, 30);
        firstDatePicker.getJFormattedTextField().setFont(datePickerFont);
        firstDatePicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                firstDateSelected = model1.getValue();
                if (firstDateSelected != null && firstDateSelected.before(minDate)) {
                    model1.setValue(minDate);
                    firstDateSelected = minDate;
                }
                updatePriceLabel(offer);
            }
        });
        add(firstDatePicker);

        // Second date picker
        UtilDateModel model2 = new UtilDateModel();
        Properties p2 = new Properties();
        p2.put("text.today", "Today");
        p2.put("text.month", "Month");
        p2.put("text.year", "Year");
        JDatePanelImpl secondDatePanel = new JDatePanelImpl(model2, p2);
        JDatePickerImpl secondDatePicker = new JDatePickerImpl(secondDatePanel, new DateComponentFormatter());
        secondDatePicker.setBounds(270, 120, 200, 30);
        secondDatePicker.getJFormattedTextField().setFont(datePickerFont);
        secondDatePicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(firstDateSelected != null){
                    secondDateSelected = model2.getValue();
                    if(secondDateSelected != null && (secondDateSelected.before(minDate) || secondDateSelected.before(firstDateSelected))){
                        Date default1 = new Date(firstDateSelected.getTime() + 24 * 60 * 60 * 1000);
                        model2.setValue(default1);
                        secondDateSelected = default1;
                    }
                    updatePriceLabel(offer);
                }
            }
        });
        add(secondDatePicker);

        // Price label
        priceLabel = new JLabel("Price: ");
        priceLabel.setBounds(30, 160, 200, 30);
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        add(priceLabel);

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
            // Need to make this functional
        });
        add(addButton);
    }

    private void updatePriceLabel(RoomOffer offer) {
        if (firstDateSelected != null && secondDateSelected != null) {
            long diffInMillies = Math.abs(secondDateSelected.getTime() - firstDateSelected.getTime());
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            double price = diffInDays * offer.getRoomPricePerNight(); // Assuming getPricePerNight() returns the price per night
            priceLabel.setText(String.format("Price: $%.2f", price));
        }
    }
}
