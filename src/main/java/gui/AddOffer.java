package gui;

import Components.RButton;
import Components.TextPrompt;
import DataBase.FireBaseService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.prefs.Preferences;

public class AddOffer extends JPanel {
    private DefaultImageController imageController;
    private DefaultNavigationController navigationController;

    public AddOffer(BaseFrame baseFrame) {
        setSize(1400, 600);
        setLayout(null);


        // Add title label
        JLabel title = new JLabel("Add Offer");
        title.setBounds(0, 0, 600, 50);
        title.setFont(new Font("Segoe UI", Font.BOLD, 35));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);


        Cursor cursor = new Cursor(Cursor.HAND_CURSOR);

        // Add location name field
        JTextField nameField = new JTextField();
        TextPrompt namePrompt = new TextPrompt("Location Name:", nameField);
        namePrompt.setFont(new Font("Dialog", Font.PLAIN, 23));
        namePrompt.setForeground(Color.decode("#AAAAAA"));
        nameField.setBounds(30, 110, getWidth() / 2 - 110 - 110, 40);
        nameField.setFont(new Font("Dialog", Font.PLAIN, 23));
        add(nameField);

        // Add price for location
        JTextField priceField = new JTextField();
        TextPrompt pricePrompt = new TextPrompt("Price:", priceField);
        pricePrompt.setFont(new Font("Dialog", Font.PLAIN, 23));
        pricePrompt.setForeground(Color.decode("#AAAAAA"));
        priceField.setBounds(30, 180, getWidth() / 2 - 220, 40);
        priceField.setFont(new Font("Dialog", Font.PLAIN, 23));
        add(priceField);

        JTextField locationField = new JTextField();
        TextPrompt locationPrompt = new TextPrompt("Location:", locationField);
        locationPrompt.setFont(new Font("Dialog", Font.PLAIN, 23));
        locationPrompt.setForeground(Color.decode("#AAAAAA"));
        locationField.setBounds(30, 250, getWidth() / 2 - 220, 40);
        locationField.setFont(new Font("Dialog", Font.PLAIN, 23));
        add(locationField);

        // Add description area
        JTextArea descriptionArea = new JTextArea();
        TextPrompt descriptionPrompt = new TextPrompt("Description:", descriptionArea);
        descriptionPrompt.setFont(new Font("Dialog", Font.PLAIN, 23));
        descriptionPrompt.setVerticalAlignment(JLabel.TOP);
        descriptionPrompt.setForeground(Color.decode("#AAAAAA"));
        descriptionArea.setBounds(30, 320, getWidth() / 2 - 110 - 110, 80);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(nameField.getBorder());
        descriptionArea.setFont(new Font("Dialog", Font.PLAIN, 23));
        add(descriptionArea);


        ArrayList<String> paths = new ArrayList<>();

        // Add upload button
        RButton uploadButton = new RButton("Upload", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        uploadButton.setCursor(cursor);
        uploadButton.setFont(new Font("Dialog", Font.PLAIN, 23));
        uploadButton.setBounds(280, 450, 220, 70);
        uploadButton.setForeground(Color.decode("#D9D9D9"));
        uploadButton.addActionListener(e -> {
            String lastDirectory = Preferences.userNodeForPackage(AddOffer.class).get("Images.lastDirectory", System.getProperty("user.home"));
            JFileChooser fc = new JFileChooser();
            File lastPath = new File(lastDirectory);
            if (lastPath.exists() && lastPath.isDirectory()) {
                fc.setCurrentDirectory(new File(lastDirectory));
            }
            fc.addChoosableFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
            fc.setMultiSelectionEnabled(true);
            if (fc.showOpenDialog(AddOffer.this) == JFileChooser.APPROVE_OPTION) {
                lastDirectory = fc.getCurrentDirectory().getPath();
                Preferences.userNodeForPackage(AddOffer.class).put("Images.lastDirectory", lastDirectory);

                File[] files = fc.getSelectedFiles();

                Image[] resizedImages = new Image[files.length];
                for (int i = 0; i < files.length; i++) {
                    try {
                        paths.add(files[i].toString());
                        Image originalImage = ImageIO.read(files[i]);
                        resizedImages[i] = originalImage.getScaledInstance(800, 450, Image.SCALE_SMOOTH);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                imageController.setModel(new DefaultImageModel(Arrays.asList(resizedImages)));
                navigationController.setModel(new DefaultNavigationModel(0, files.length));
            }
        });
        add(uploadButton);

        // Add add button
        RButton addButton = new RButton("Add", Color.decode("#7A4641"), Color.decode("#512E2B"), Color.decode("#8D4841"));
        addButton.setCursor(cursor);
        addButton.setFont(new Font("Dialog", Font.PLAIN, 23));
        addButton.setBounds(30, 450, 220, 70);
        addButton.setForeground(Color.decode("#D9D9D9"));
        addButton.addActionListener(e ->{

            String locationName = nameField.getText();
            String locationDescription = descriptionArea.getText();
            Double locationPrice = Double.valueOf(priceField.getText());
            ArrayList<String> imageNames = new ArrayList<>();
            for(String path : paths){
                int lastIndexOffset = path.lastIndexOf("\\");
                imageNames.add(path.substring(lastIndexOffset + 1));
            }
            try {
                FireBaseService.uploadImages(paths, imageNames);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            FireBaseService.registerLocation(locationName, locationDescription, locationPrice, imageNames);
            baseFrame.dispose();
        });

        add(addButton);

        // Add image view
        ImageView imageView = new DefaultImageViewPane();
        ImageModel imageModel = new DefaultImageModel(new ArrayList<>());
        imageController = new DefaultImageController(imageView, imageModel);
        JScrollPane imageScrollPane = new JScrollPane(imageController.getView().getView());
        imageScrollPane.setBounds(540, 40, 810, 460);
        add(imageScrollPane);

        // Add navigation controller
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
        JPanel navPanel = (JPanel) navigationController.getView().getView();
        navPanel.setPreferredSize(new Dimension(100, getHeight()));
        navPanel.setBounds(750, 480, 300, getHeight() / 6);
        add(navPanel);

    }
}

