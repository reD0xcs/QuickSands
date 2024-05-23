package gui;

import Components.RButton;
import Components.RoomOffer;
import Stripe.StripePaymentProcessor;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.PaymentMethodCreateParams.CardDetails;
import com.stripe.model.PaymentMethod;
import com.stripe.param.checkout.SessionCreateParams;
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
import java.io.FileNotFoundException;
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
    private double finalPrice;

    // Payment form elements
    private JTextField cardNumberField;
    private JTextField expirationDateField;
    private JTextField cvcField;
    private JTextField cardholderNameField;

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
        priceLabel.setBounds(30, 160, 350, 30);
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

        // Add card payment form elements
        JLabel cardNumberLabel = new JLabel("Card Number:");
        cardNumberLabel.setBounds(30, 200, 200, 30);
        cardNumberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(cardNumberLabel);

        cardNumberField = new JTextField();
        cardNumberField.setBounds(150, 200, 200, 30);
        cardNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(cardNumberField);

        JLabel expirationDateLabel = new JLabel("Expiration Date (MM/YY):");
        expirationDateLabel.setBounds(30, 240, 200, 30);
        expirationDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(expirationDateLabel);

        expirationDateField = new JTextField();
        expirationDateField.setBounds(230, 240, 120, 30);
        expirationDateField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(expirationDateField);

        JLabel cvcLabel = new JLabel("CVC:");
        cvcLabel.setBounds(30, 280, 200, 30);
        cvcLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(cvcLabel);

        cvcField = new JTextField();
        cvcField.setBounds(150, 280, 100, 30);
        cvcField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(cvcField);

        JLabel cardholderNameLabel = new JLabel("Cardholder Name:");
        cardholderNameLabel.setBounds(30, 320, 200, 30);
        cardholderNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(cardholderNameLabel);

        cardholderNameField = new JTextField();
        cardholderNameField.setBounds(180, 320, 200, 30);
        cardholderNameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(cardholderNameField);


        // Add submit button for payment
        RButton submitButton = new RButton("Submit Payment", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        submitButton.setCursor(cursor);
        submitButton.setFont(new Font("Dialog", Font.PLAIN, 23));
        submitButton.setBounds(150, 420, 220, 70);
        submitButton.setForeground(Color.decode("#D9D9D9"));
        submitButton.addActionListener(e -> {
            processPayment();
        });
        add(submitButton);
    }

    private void updatePriceLabel(RoomOffer offer) {
        if (firstDateSelected != null && secondDateSelected != null) {
            long diffInMillies = Math.abs(secondDateSelected.getTime() - firstDateSelected.getTime());
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            // Calculate the number of 2-night pairs
            long twoNightPairs = diffInDays / 2;

            // Calculate the total price without discount
            double originalPrice = diffInDays * offer.getRoomPricePerNight();

            // Apply the progressive discount
            double discount = twoNightPairs * 0.04 * originalPrice;

            // Calculate the final price after discount
            finalPrice = originalPrice - discount;

            // Calculate the reduction amount
            double reduction = originalPrice - finalPrice;

            // Update the price label with original price (strikethrough), final price, and reduction amount
            if (discount > 0) {
                priceLabel.setText(String.format(
                        "<html>Price: <span style='text-decoration: line-through; color: black;'>$%.2f</span> " +
                                "<span style='color: red;'>$%.2f</span> " +
                                "<span style='color: red;'>(-$%.2f)</span></html>",
                        originalPrice, finalPrice, reduction
                ));
            } else {
                priceLabel.setText(String.format(
                        "<html>Price: <span style='color: black;'>$%.2f</span></html>",
                        originalPrice
                ));
            }
        }
    }


    private void processPayment() {
        String cardNumber = cardNumberField.getText();
        String expirationDate = expirationDateField.getText();
        String cvc = cvcField.getText();
        String cardholderName = cardholderNameField.getText();

        // Simulate payment processing
        if (cardNumber.isEmpty() || expirationDate.isEmpty() || cvc.isEmpty() || cardholderName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all payment details.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            try {
                // Step 1: Process payment and retrieve PaymentIntent
                StripePaymentProcessor processor = new StripePaymentProcessor();
                PaymentIntent paymentIntent = processor.createPaymentIntent(finalPrice);

                // Step 2: Handle the PaymentIntent status and create Customer and Payment Method if payment is successful
                if ("requires_payment_method".equals(paymentIntent.getStatus())) {
                    // Create PaymentMethod
                    PaymentMethodCreateParams paymentMethodParams = PaymentMethodCreateParams.builder()
                            .setType(PaymentMethodCreateParams.Type.CARD)
                            .setCard(CardDetails.builder().setNumber(cardNumber)
                            .setExpMonth(Long.parseLong(expirationDate.substring(0, 2)))
                            .setExpYear(Long.parseLong("20" + expirationDate.substring(3)))  // Assuming YY format
                            .setCvc(cvc)
                            .build())
                            .build();

                    PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);

                    // Handle success scenario
                    JOptionPane.showMessageDialog(this, "Payment successful! Thank you for your reservation.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Handle other statuses if needed
                    JOptionPane.showMessageDialog(this, "Payment failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (StripeException e) {
                // Handle Stripe API exceptions
                JOptionPane.showMessageDialog(this, "Stripe API error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (NumberFormatException e) {
                // Handle number format exception for expiration date parsing
                JOptionPane.showMessageDialog(this, "Invalid expiration date format.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (Exception e) {
                // Handle other exceptions
                JOptionPane.showMessageDialog(this, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

}

