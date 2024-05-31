package gui;

import Components.EmailSender;
import Components.QRCodeGenerator;
import Components.RButton;
import Components.RoomOffer;
import DataBase.FireBaseService;
import DataBase.User;
import Stripe.StripeConfig;
import Stripe.StripePaymentProcessor;
import Stripe.PaymentConfirmationService;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.PaymentMethodCreateParams.CardDetails;
import com.stripe.model.PaymentMethod;
import com.stripe.param.checkout.SessionCreateParams;
import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AddReservation extends JPanel {
    private DefaultImageController imageController = new DefaultImageController(new DefaultImageViewPane(), new DefaultImageModel(new ArrayList<>()));
    private DefaultNavigationController navigationController = new DefaultNavigationController(new DefaultNavigationView(), new DefaultNavigationModel(0, 4));
    private java.util.Date firstDateSelected = null;
    private java.util.Date secondDateSelected = null;
    private JLabel priceLabel;
    private double finalPrice;
    private User user;
    private final PaymentConfirmationService confirmationService = new PaymentConfirmationService();
    // Payment form elements
    private JTextField cardNumberField;
    private JTextField expirationDateField;
    private JTextField cvcField;
    private JTextField cardholderNameField;
    private RoomOffer offer;

    public AddReservation(BaseFrame baseFrame, RoomOffer o, User u) {
        setSize(1400, 600);
        setLayout(null);
        user = u;
        offer = o;
        JLabel title = new JLabel(Translator.getValue("addreservation"));
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
                updatePriceLabel(offer);
            }
        });
        add(firstDatePicker);

        // Second date picker
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
                    updatePriceLabel(offer);
                }
            }
        });
        add(secondDatePicker);

        // Price label
        priceLabel = new JLabel(Translator.getValue("price"));
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

        JLabel cardholderNameLabel = new JLabel(Translator.getValue("cardholdername"));
        cardholderNameLabel.setBounds(30, 320, 200, 30);
        cardholderNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(cardholderNameLabel);

        cardholderNameField = new JTextField();
        cardholderNameField.setBounds(180, 320, 200, 30);
        cardholderNameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        add(cardholderNameField);


        // Add submit button for payment
        RButton submitButton = new RButton(Translator.getValue("submitpayment"), Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        submitButton.setCursor(cursor);
        submitButton.setFont(new Font("Dialog", Font.PLAIN, 23));
        submitButton.setBounds(150, 420, 220, 70);
        submitButton.setForeground(Color.decode("#D9D9D9"));
        submitButton.addActionListener(e -> {
            processPayment(baseFrame);
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
    private byte[] createPdfReceipt(String cardholderName, String cardNumber, String expirationDate, double amount) {
        String dest = "receipt.pdf";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add receipt title
            Paragraph title = new Paragraph("Payment Receipt")
                    .setBold()
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Define table columns and styling
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .useAllAvailableWidth();

            // Add cardholder name
            table.addCell(new Paragraph("Cardholder Name:").setBold().setBackgroundColor(new DeviceRgb(240, 240, 240)));
            table.addCell(new Paragraph(cardholderName));

            // Add card number
            table.addCell(new Paragraph("Card Number:").setBold().setBackgroundColor(new DeviceRgb(240, 240, 240)));
            table.addCell(new Paragraph("**** **** **** " + cardNumber.substring(cardNumber.length() - 4)));

            // Add expiration date
            table.addCell(new Paragraph("Expiration Date:").setBold().setBackgroundColor(new DeviceRgb(240, 240, 240)));
            table.addCell(new Paragraph(expirationDate));

            // Add amount
            table.addCell(new Paragraph("Amount:").setBold().setBackgroundColor(new DeviceRgb(240, 240, 240)));
            table.addCell(new Paragraph("$" + String.format("%.2f", amount)));

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    private void processPayment(BaseFrame baseFrame) {
        String cardNumber = cardNumberField.getText();
        String expirationDate = expirationDateField.getText();
        String cvc = cvcField.getText();
        String cardholderName = cardholderNameField.getText();

        if (!validateCardNumber(cardNumber)) {
            JOptionPane.showMessageDialog(this, "Invalid card number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateExpirationDate(expirationDate)) {
            JOptionPane.showMessageDialog(this, "Invalid expiration date.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateCVC(cvc)) {
            JOptionPane.showMessageDialog(this, "Invalid CVC.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateCardholderName(cardholderName)) {
            JOptionPane.showMessageDialog(this, "The name should only contain letters.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            try {
                PaymentIntent paymentIntent = StripeConfig.createPaymentIntent(finalPrice);
                PaymentIntent confirmation = StripeConfig.confirmPaymentIntent(paymentIntent.getId(), "pm_card_visa");
                FireBaseService.savePaymentConfirmation(user.getEmail(), paymentIntent.getId(), confirmation.getId());
                int result = JOptionPane.showConfirmDialog(this, "Payment successful! Thank you for your reservation.", "Success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                createPdfReceipt(cardholderName, cardNumber, expirationDate, finalPrice);
                String code = offer.getRoomId() + "_" + user.getFirst_name() + new Date().getTime();
                String codeName = code + ".png";
                BufferedImage QRCode = QRCodeGenerator.generateQRCodeImage(code, 300, 300);
                String receiptName = code + ".pdf";
                byte[] receipt = createPdfReceipt(cardholderName, cardNumber, expirationDate, finalPrice);

                FireBaseService.uploadQR(codeName, QRCode);
                FireBaseService.uploadPDF(receiptName, receipt);
                FireBaseService.registerReservation(user.getEmail(), firstDateSelected, secondDateSelected, finalPrice, codeName, receiptName, offer.getRoomId());
                try {
                    EmailSender.sendEmail(user, QRCode, receipt);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (WriterException ex) {
                    throw new RuntimeException(ex);
                }
                if(result == JOptionPane.OK_OPTION){
                    baseFrame.dispose();
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
    private boolean validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 13 || cardNumber.length() > 19) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 2; i >= 0; i--) {
            int n = cardNumber.charAt(i) - '0';
            if(n < 0 || n > 9){
                return false;
            }
            if (alternate) {
                n *= 2;
            }
            alternate = !alternate;
            sum += n > 9 ? n - 9 : n;
        }
        int checksum = cardNumber.charAt(cardNumber.length() - 1) - '0';
        return (sum + checksum) % 10 == 0;
    }

    private boolean validateExpirationDate(String expirationDate) {
        if (expirationDate == null || !expirationDate.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
        sdf.setLenient(false);
        try {
            Date expDate = sdf.parse(expirationDate);
            return expDate.after(new Date());
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean validateCVC(String cvc) {
        return cvc != null && cvc.matches("\\d{3,4}");
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
    public boolean validateCardholderName(String cardholderName) {
        if (cardholderName == null || cardholderName.isEmpty()) {
            return false;
        }

        // Regular expression to match only letters and spaces
        String regex = "^[a-zA-Z\\s]+$";
        return cardholderName.matches(regex);
    }
}

