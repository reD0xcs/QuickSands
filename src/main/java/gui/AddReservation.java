package gui;

import Components.HotelOffer;
import Components.RButton;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class AddReservation extends JPanel {
    private DefaultImageController imageController = new DefaultImageController(new DefaultImageViewPane(), new DefaultImageModel(new ArrayList<>()));
    private DefaultNavigationController navigationController = new DefaultNavigationController(new DefaultNavigationView(), new DefaultNavigationModel(0, 4));

    public AddReservation(BaseFrame baseFrame, HotelOffer offer, int index) {
        setSize(1400, 600);
        setLayout(null);

        // Clear previous components if any

        JLabel title = new JLabel("Add Reservation");
        title.setBounds(0, 20, 600, 50);
        title.setFont(new Font("Segoe UI", Font.BOLD, 35));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        Image[] resizedImages = new Image[offer.getHotelImages().size()];
        for (int i = 0; i < offer.getHotelImages().size(); i++) {
            resizedImages[i] = offer.getHotelImages().get(i).getScaledInstance(800, 450, Image.SCALE_SMOOTH);
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
        RButton addButton = new RButton("Add", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        addButton.setCursor(cursor);
        addButton.setFont(new Font("Dialog", Font.PLAIN, 23));
        addButton.setBounds(30, 380, 220, 70);
        addButton.setForeground(Color.decode("#D9D9D9"));
        addButton.addActionListener(e -> {
            imageController.setModel(new DefaultImageModel(new ArrayList<>()));
            baseFrame.dispose(); // Close the current window
            // Reset image controller for reuse
        });
        add(addButton);
    }
}