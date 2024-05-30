package DataBase;

import Components.HotelOffer;
import Components.Reservation;
import Components.RoomOffer;
import Components.roomTypes;
import Stripe.StripeConfig;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.*;
import com.google.cloud.storage.Blob;
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
import java.util.List;
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
    public static void uploadQR(String qrName, BufferedImage qrCode) throws IOException {
        String serviceKey = "src/main/resources/key.json";
        FileInputStream serviceAccountStream = new FileInputStream(serviceKey);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);
        com.google.cloud.storage.Storage storage = (Storage) StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        String bucketName = "proiectc-173b4.appspot.com";

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(qrCode, "png", os);

        BlobId blobId = BlobId.of(bucketName, "qr-codes/" + qrName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/png").build();
        storage.create(blobInfo, new ByteArrayInputStream(os.toByteArray()));
    }
    public static void uploadPDF(String pdfName, byte[] pdfData) throws IOException {
        String serviceKey = "src/main/resources/key.json";
        FileInputStream serviceAccountStream = new FileInputStream(serviceKey);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);
        com.google.cloud.storage.Storage storage = (Storage) StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        String bucketName = "proiectc-173b4.appspot.com";
        BlobId blobId = BlobId.of(bucketName, "pdfs/" + pdfName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/pdf").build();
        storage.create(blobInfo, pdfData);
    }
    public static void registerOffer(String description, Double price, ArrayList<String> imageNames, roomTypes type){
        final DocumentReference counterRef = database.collection("counter").document("counter");

        try{
            database.runTransaction((Transaction.Function<Void>) transaction ->{
                DocumentSnapshot snapshot = transaction.get(counterRef).get();
                long newId;
                if(snapshot.exists()){
                    newId = snapshot.getLong("counter") + 1;
                    transaction.update(counterRef, "counter", newId);
                }
                else{
                    newId = 1;
                    Map<String, Object> counterData = new HashMap<>();
                    counterData.put("counter", newId);
                    transaction.set(counterRef, counterData);
                }

                Map<String, Object> offerData = new HashMap<>();
                offerData.put("description", description);
                offerData.put("price", price);
                offerData.put("imageNames", imageNames);
                offerData.put("type", type.toString());
                offerData.put("index", newId);

                DocumentReference newDocRef = database.collection("offers").document(String.valueOf(newId));
                transaction.set(newDocRef, offerData);
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public static void saveCustomerToFirebase(String userEmail, String customerId){
        FirestoreClient.getFirestore().collection("stripe_customers").document(userEmail).set(new StripeConfig.StripeCustomer(customerId));
    }
    public static void registerReservation(String email, Date checkInDate, Date checkOutDate, Double price, String qrCodeName, String pdfName, Long roomIndex){
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("userEmail", email);
            data.put("checkInDate", checkInDate);
            data.put("checkOutDate", checkOutDate);
            data.put("price", price);
            data.put("qrCodeName", qrCodeName);
            data.put("pdfName", pdfName);
            data.put("roomIndex", roomIndex);
            database.collection("reservations").add(data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void savePaymentConfirmation(String userEmail, String paymentIntentId, String confimationId){
        Map<String, Object> data = new HashMap<>();
        data.put("confirmationId", confimationId);
        data.put("paymentIntentId", paymentIntentId);
        FirestoreClient.getFirestore().collection("stripe_customers")
                .document(userEmail)
                .collection("payments")
                .add(data);
    }
    public static ArrayList<RoomOffer> loadAllOffers(){
        ArrayList<RoomOffer> offers = new ArrayList<>();
        try{
            QuerySnapshot querySnapshot = database.collection("offers").get().get();
            for(QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> doc = (Map<String, Object>) document.getData();
                Long id = (Long) doc.get("index");
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
    public static ArrayList<Reservation> loadReservations(User user) {
        ArrayList<Reservation> reservations = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = database.collection("reservations").whereEqualTo("userEmail", user.getEmail()).get();
            QuerySnapshot querySnapshot = future.get();
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                Map<String, Object> doc = document.getData();
                Timestamp checkInTimestamp = (Timestamp) doc.get("checkInDate");
                Timestamp checkOutTimestamp = (Timestamp) doc.get("checkOutDate");
                Date checkInDate = checkInTimestamp.toDate();
                Date checkOutDate = checkOutTimestamp.toDate();
                String pdfName = (String) doc.get("pdfName");
                Double price = (Double) doc.get("price");
                String qrCodeName = (String) doc.get("qrCodeName");
                Long index = (Long) doc.get("roomIndex");
                String userEmail = (String) doc.get("userEmail");
                Reservation reservation = new Reservation(userEmail, checkInDate, checkOutDate, qrCodeName, pdfName, price, index);
                reservations.add(reservation);
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return reservations;
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
    public static Image loadQR(String QRName) throws IOException {
        String serviceKey = "src/main/resources/key.json";
        FileInputStream serviceAccountStream = new FileInputStream(serviceKey);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);
        com.google.cloud.storage.Storage storage = (Storage) StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        String bucketName = "proiectc-173b4.appspot.com";

        Blob blob = storage.get(bucketName, "qr-codes/" +  QRName);
        if(blob != null){
            byte[] imageData = blob.getContent();
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageData));
            return bufferedImage;
        }
        return null;
    }
    public static byte[] loadPDF(String PDFName) throws IOException {
        String serviceKey = "src/main/resources/key.json";
        FileInputStream serviceAccountStream = new FileInputStream(serviceKey);
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccountStream);
        com.google.cloud.storage.Storage storage = (Storage) StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        String bucketName = "proiectc-173b4.appspot.com";

        Blob blob = storage.get(bucketName, "pdfs/" +  PDFName);
        if(blob != null){
            byte[] imageData = blob.getContent();
            return imageData;
        }
        return null;
    }
    public static void cancelReservation(String QRName) throws ExecutionException, InterruptedException {
        CollectionReference reference = database.collection("reservations");
        Query query = reference.whereEqualTo("qrCodeName", QRName);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QueryDocumentSnapshot reservationDoc = querySnapshot.get().getDocuments().isEmpty() ? null : querySnapshot.get().getDocuments().get(0);

        if (reservationDoc != null) {
            // Get the ID of the reservation document
            String reservationId = reservationDoc.getId();

            // Reference to the reservation document in the 'reservations' collection
            DocumentReference reservationRef = database.collection("reservations").document(reservationId);

            // Perform batched operations to move the document
            WriteBatch batch = database.batch();

            // Reference to the new document in the 'canceled_reservations' collection
            DocumentReference canceledReservationRef = database.collection("canceled_reservations").document(reservationId);

            // Set the data to the new document in 'canceled_reservations' collection
            batch.set(canceledReservationRef, reservationDoc.getData());

            // Delete the original document from 'reservations' collection
            batch.delete(reservationRef);

            // Commit the batch
            ApiFuture<List<WriteResult>> batchResult = batch.commit();
            batchResult.get(); // Wait for the batch to complete

        }
    }

    public static ArrayList<RoomOffer> getAvailableRoomOffers(Date checkInDate, Date checkOutDate) {
        ArrayList<RoomOffer> availableRoomOffers = new ArrayList<>();
        try {
            // Load all room offers
            ArrayList<RoomOffer> allRoomOffers = loadAllOffers();

            // Fetch reservations within the date range
            CollectionReference reservationsRef = database.collection("reservations");
            ApiFuture<QuerySnapshot> future = reservationsRef.get();
            QuerySnapshot querySnapshot = future.get();

            Set<Long> reservedRoomIndices = new HashSet<>();

            // Filter reservations that overlap with the given date range
            for (QueryDocumentSnapshot document : querySnapshot.getDocuments()) {
                Date reservationCheckInDate = document.getDate("checkInDate");
                Date reservationCheckOutDate = document.getDate("checkOutDate");
                Long roomIndex = document.getLong("roomIndex");

                // Check if the reservation overlaps with the requested date range
                if (datesOverlap(checkInDate, checkOutDate, reservationCheckInDate, reservationCheckOutDate)) {
                    reservedRoomIndices.add(roomIndex);
                }
            }

            // Filter room offers that do not have reservations in the given date interval
            for (RoomOffer offer : allRoomOffers) {
                if (!reservedRoomIndices.contains(offer.getRoomId())) {
                    availableRoomOffers.add(offer);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            // Handle exception appropriately based on your application's error handling strategy
        }
        return availableRoomOffers;
    }

    // Utility method to check if two date ranges overlap
    private static boolean datesOverlap(Date start1, Date end1, Date start2, Date end2) {
        return start1.before(end2) && end1.after(start2);
    }

}
