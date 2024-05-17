package gui;

import Components.HotelOffer;
import DataBase.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class ViewPanel extends BasePanel{

    private ArrayList<HotelOffer> hotelOffers;
    private final User user;
    public ViewPanel(BaseFrame baseFrame, User u){

        user = u;
        JPanel panel = new JPanel();
        addComponents(baseFrame, panel);
    }
    @Override
    public void addComponents(BaseFrame baseFrame, JPanel componentsPanel) {
        componentsPanel.setLayout(new BorderLayout());

        JPanel offersPanel = new JPanel();
        offersPanel.setLayout(new BoxLayout(offersPanel, BoxLayout.Y_AXIS));

        for(HotelOffer offer: hotelOffers){
            offersPanel.add(createOfferPanel(offer));
        }
        JScrollPane scrollPane = new JScrollPane(offersPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        componentsPanel.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void addComponents(BaseFrame frame) {

    }
    private JPanel createOfferPanel(HotelOffer offer){
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel hotelNameLabel = new JLabel(offer.getHotelName());
        JLabel hotelPriceLabel = new JLabel("Price:" + offer.getHotelprice());
        JButton bookButton = new JButton("Book Now");
        bookButton.addActionListener(e -> {
            // Handle booking logic here
            JOptionPane.showMessageDialog(this, "Booking " + offer.getHotelName());
        });

        // Add components to the panel
        panel.add(hotelNameLabel, BorderLayout.NORTH);
        panel.add(hotelPriceLabel, BorderLayout.CENTER);
        panel.add(bookButton, BorderLayout.SOUTH);

        return panel;

    }
}
