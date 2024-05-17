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
    private DefaultImageController imageController;
    private DefaultNavigationController navigationController;

    public AddReservation(BaseFrame baseFrame, ArrayList<HotelOffer> hotelOffers, int index) {
        setSize(1400, 600);
        setLayout(null);

        // Clear previous components if any
        removeAll();

        JLabel title = new JLabel("Add Reservation");
        title.setBounds(0, 20, 600, 50);
        title.setFont(new Font("Segoe UI", Font.BOLD, 35));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        // Create image view and controller
        ImageView imageView = new DefaultImageViewPane();
        ImageModel imageModel = new DefaultImageModel(hotelOffers.get(index).getHotelImages());
        imageController = new DefaultImageController(imageView, imageModel);
        imageController.setModel(imageModel);

        // Create navigation controller
        NavigationView navView = new DefaultNavigationView();
        NavigationModel navModel = new DefaultNavigationModel(0, hotelOffers.get(index).getHotelImages().size());
        navigationController = new DefaultNavigationController(navView, navModel);
        navigationController.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                NavigationController controller = (NavigationController) e.getSource();
                imageController.loadImageAt(controller.getModel().getIndex());
            }
        });

        // Load images for the current hotel offer
        Image[] resizedImages = new Image[hotelOffers.get(index).getHotelImages().size()];
        for (int i = 0; i < hotelOffers.get(index).getHotelImages().size(); i++) {
            resizedImages[i] = hotelOffers.get(index).getHotelImages().get(i).getScaledInstance(800, 450, Image.SCALE_SMOOTH);
        }
        imageController.setModel(new DefaultImageModel(Arrays.asList(resizedImages)));
        navigationController.setModel(new DefaultNavigationModel(0, hotelOffers.get(index).getHotelImages().size()));

        // Add image scroll pane and navigation panel to this panel
        JScrollPane imageScrollPane = new JScrollPane(imageController.getView().getView());
        imageScrollPane.setBounds(540, 40, 810, 460);
        add(imageScrollPane);

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