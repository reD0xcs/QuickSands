package DataBase;

import Components.HotelOffer;
import Components.RoomOffer;
import Components.roomTypes;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;

import javax.imageio.ImageIO;
import javax.swing.text.html.BlockView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class FireBaseService {
    private static volatile FireBaseService instance;
    private static Firestore database;

    private FireBaseService() {
        if (instance != null) {
            throw new IllegalStateException("FireBaseService instance already exists.");
        }
    }

    public static FireBaseService getInstance() {
        if (instance == null) {
            synchronized (FireBaseService.class) {
                if (instance == null) {
                    instance = new FireBaseService();
                    initializeFirebase();
                }
            }
        }
        return instance;
    }

    private static void initializeFirebase() {
        try {
            InputStream serviceAccount = FireBaseService.class.getClassLoader().getResourceAsStream("key.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://proiectc-173b4-default-rtdb.europe-west1.firebasedatabase.app")
                    .setStorageBucket("proiectc-173b4.appspot.com")
                    .build();
            FirebaseApp.initializeApp(options);
            database = FirestoreClient.getFirestore();
        } catch (IOException e) {
            e.printStackTrace(); // Handle initialization failure appropriately
        }
    }

    public Firestore getDatabase() {
        return database;
    }

    public static User verifyAccount(String email, String password) {
        try {
            Query query = database.collection("users").whereEqualTo("email", email);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            QuerySnapshot snapshot = querySnapshot.get();
            if (!snapshot.isEmpty()) {
                for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
                    String dbPassword = document.getString("password");
                    if (dbPassword != null && dbPassword.equals(password)) {
                        return new User(email, document.getString("first_name"),
                                document.getString("last_name"), password, document.getString("role"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle database access failure appropriately
        }
        return null;
    }

    public static User registerUser(String firstName, String lastName, String email, String password, String role) {
        try {
            Map<String, Object> userData = new HashMap<>();
            userData.put("first_name", firstName);
            userData.put("last_name", lastName);
            userData.put("email", email);
            userData.put("password", password);
            userData.put("role", role);
            database.collection("users").add(userData);
            return new User(email, firstName, lastName, password, role);
        } catch (Exception e) {
            e.printStackTrace(); // Handle database access failure appropriately
        }
        return null;
    }
    public static void uploadImages(ArrayList<String> imagePaths, ArrayList<String> imageNames) throws IOException {
        String serviceKey = "src/main/resources/key.json";
        FileInputStream serviceAccountStream = new FileInputStream(serviceKey);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);
        com.google.cloud.storage.Storage storage = (Storage) StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        String bucketName = "proiectc-173b4.appspot.com";

        for(int i = 0; i < imagePaths.size(); i++) {
            storage.create(
                    BlobInfo.newBuilder(bucketName, imageNames.get(i)).build(),
                    Files.newInputStream(Paths.get(imagePaths.get(i)))
            );
        }
    }
    public static void registerLocation(String name, String description, Double price, ArrayList<String> imageNames, String locationPlace){
        try{
            Map<String, Object> locationData = new HashMap<>();
            locationData.put("name", name);
            locationData.put("description", description);
            locationData.put("price", price);
            locationData.put("imageNames", imageNames);
            locationData.put("location", locationPlace);
            database.collection("locations").add(locationData);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void registerOffer(String description, Double price, ArrayList<String> imageNames, roomTypes type){
        try{
            Map<String, Object> offerData = new HashMap<>();
            offerData.put("description", description);
            offerData.put("price", price);
            offerData.put("imageNames", imageNames);
            offerData.put("type", type.toString());
            database.collection("offers").add(offerData);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static ArrayList<RoomOffer> loadAllOffers(){
        ArrayList<RoomOffer> offers = new ArrayList<>();
        try{
            QuerySnapshot querySnapshot = database.collection("offers").get().get();
            for(QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> doc = (Map<String, Object>) document.getData();
                String id = (String) doc.get("id");
                String description = (String) doc.get("description");
                Double price = (Double) doc.get("price");
                ArrayList<String> imagesNames = (ArrayList<String>) doc.get("imageNames");
                String roomType = (String) doc.get("type");
                ArrayList<Image> images = downloadImages(imagesNames);

                RoomOffer offer = new RoomOffer(id, description, price, roomType, images);
                offers.add(offer);
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return offers;
    }

    public static ArrayList<Image> downloadImages(ArrayList<String> imageNames) throws IOException {
        ArrayList<Image> images = new ArrayList<>();
        String serviceKey = "src/main/resources/key.json";
        FileInputStream serviceAccountStream = new FileInputStream(serviceKey);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);
        com.google.cloud.storage.Storage storage = (Storage) StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        String bucketName = "proiectc-173b4.appspot.com";

        for(int i = 0; i < imageNames.size(); i++){
            Blob blob = storage.get(bucketName, imageNames.get(i));
            if(blob != null){
                byte[] imageData = blob.getContent();
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
                images.add(bufferedImage);
            }
        }

        return images;
    }
}
