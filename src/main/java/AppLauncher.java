import DataBase.FireBaseService;
import Stripe.StripeConfig;
import DataBase.User;
import gui.BaseFrame;
import gui.ProfilePanel;
import javax.swing.*;
import java.io.IOException;

import com.stripe.Stripe;

public class AppLauncher {
    public static void main(String[] args) throws IOException {

        FireBaseService.getInstance();

        String stripeApiKey = StripeConfig.getApiKey();
        if(stripeApiKey != null){
            Stripe.apiKey = stripeApiKey;
        }
        else{
            System.err.println("Stripe API key not found!");

        }

        SwingUtilities.invokeLater(() -> {
            BaseFrame baseFrame = new BaseFrame();
            //baseFrame.changePanel(new ProfilePanel(baseFrame, new User())); // Testing on the Profile Panel
            baseFrame.setVisible(true);
        });
    }
}
