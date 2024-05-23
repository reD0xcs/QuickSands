package Stripe;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class StripeConfig {
    public static String getApiKey() throws FileNotFoundException {
        String apiKey = null;
        try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/stripe_key.txt"))){
            apiKey = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return apiKey;
    }
}
