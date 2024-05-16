import DataBase.FireBaseService;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firestore.v1.WriteRequest;
import gui.BaseFrame;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

public class AppLauncher {
    public static void main(String[] args) throws IOException {
/*
        FireBaseService.getInstance();

        SwingUtilities.invokeLater(() -> {
            BaseFrame baseFrame = new BaseFrame();
            baseFrame.setVisible(true);
        });
    */
        //Logic for upload/download/load images from/to firebase storage
        /*
        FileInputStream serviceAccountStream = new FileInputStream("src/main/resources/key.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        // Path to the image file you want to upload
        String imagePath = "src/main/resources/adminProfile.png";

        // Name of the image file in Firebase Storage
        String imageName = "image.jpg";

        // Bucket name in Firebase Storage
        String bucketName = "proiectc-173b4.appspot.com";

        String downloadPath = System.getProperty("user.home") + File.separator + "image.jpg";

        BlobId blobId = BlobId.of(bucketName, imageName);
        Blob blob = storage.get(blobId);
        if (blob != null) {
            try (FileOutputStream fileOutputStream = new FileOutputStream(downloadPath)) {
                fileOutputStream.write(blob.getContent());
                System.out.println("Image downloaded successfully.");
            } catch (IOException e) {
                System.err.println("Failed to download image: " + e.getMessage());
            }
        } else {
            System.err.println("Image not found in Firebase Storage.");
        }
        System.out.println("Image uploaded successfully.");

        import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseStorageUploader {

    public static void main(String[] args) throws IOException {
        // Path to your Firebase service account key JSON file
        String serviceAccountKeyPath = "path/to/serviceAccountKey.json";

        // Initialize Firebase
        FileInputStream serviceAccountStream = new FileInputStream(serviceAccountKeyPath);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        // Path to the image file you want to upload
        String imagePath = "path/to/image.jpg";

        // Name of the image file in Firebase Storage
        String imageName = "image.jpg";

        // Bucket name in Firebase Storage
        String bucketName = "your-firebase-storage-bucket-name";

        // Upload the image
        storage.create(
            BlobInfo.newBuilder(bucketName, imageName).build(),
            Files.newInputStream(Paths.get(imagePath))
        );

        System.out.println("Image uploaded successfully.");
    }
}

        */

    }
}
