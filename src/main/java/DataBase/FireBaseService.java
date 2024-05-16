package DataBase;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FireBaseService {
    private static FireBaseService instance;
    private Firestore database;

    public FireBaseService() throws IOException{
        File file = new File(
          Objects.requireNonNull(getClass().getClassLoader().getResource("key.json")).getFile()
        );
        FileInputStream fis = new FileInputStream(file);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(fis))
                .setDatabaseUrl("https://proiectc-173b4-default-rtdb.europe-west1.firebasedatabase.app")
                .build();
        FirebaseApp.initializeApp(options);

        database = FirestoreClient.getFirestore();
    }
    public Firestore getDatabase(){
        return database;
    }

    public static FireBaseService getInstance() throws IOException{
        if(instance == null){
            instance = new FireBaseService();
        }
        return instance;
    }

    public static User verifyAccount(String email, String password) throws IOException {
        try{
            Firestore database = FireBaseService.getInstance().getDatabase();
            Query query = database.collection("users").whereEqualTo("email", email);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            QuerySnapshot snapshot = querySnapshot.get();
            if(!snapshot.isEmpty()){
                for(QueryDocumentSnapshot document : snapshot.getDocuments()){
                    String dbPassword = document.getString("password");

                    if(dbPassword.equals(password)){
                        return new User(email, document.getString("first_name"), document.getString("last_name"), password, document.getString("role"));
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static User registerUser(String firstName, String lastName, String email, String password, String role) throws IOException {
        try{
            Firestore database = FireBaseService.getInstance().getDatabase();
            CollectionReference docref = database.collection("users");
            Map<String, Object> userData = new HashMap<>();
            userData.put("first_name", firstName);
            userData.put("last_name", lastName);
            userData.put("email", email);
            userData.put("password", password);
            userData.put("role", role);

            docref.add(userData);
            User user = new User(email, firstName, lastName, password, role);
            return user;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
