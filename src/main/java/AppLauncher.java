import Components.QRCodeGenerator;
import DataBase.FireBaseService;
import Stripe.StripeConfig;
import DataBase.User;
import com.google.zxing.WriterException;
import gui.BaseFrame;
import gui.ProfilePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.stripe.Stripe;
import gui.SelectDatePanel;

public class AppLauncher {
    public static void main(String[] args) throws IOException, WriterException {


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
            baseFrame.changePanel(new SelectDatePanel(baseFrame, new User())); // Testing on the Profile Panel
            baseFrame.setVisible(true);
        });
    }
}
