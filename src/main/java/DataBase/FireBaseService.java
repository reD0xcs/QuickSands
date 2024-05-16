package DataBase;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
}
