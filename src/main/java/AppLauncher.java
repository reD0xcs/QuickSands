import DataBase.FireBaseService;
import DataBase.User;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firestore.v1.WriteRequest;
import gui.BaseFrame;
import gui.ProfilePanel;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class AppLauncher {
    public static void main(String[] args) throws IOException {

        FireBaseService.getInstance();

        SwingUtilities.invokeLater(() -> {
            BaseFrame baseFrame = new BaseFrame();
            //baseFrame.changePanel(new ProfilePanel(baseFrame, new User())); // Testing on the Profile Panel
            baseFrame.setVisible(true);
        });
    }
}
