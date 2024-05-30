package Stripe;

import DataBase.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Order;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;

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
    public static Customer createStripeCustomer(User user) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(user.getEmail())
                .setName(user.getFirst_name() + " " + user.getLast_name())
                .build();
        Customer customer = Customer.create(params);

        return customer;
    }
    public static PaymentIntent createPaymentIntent(Double price) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (price * 100))
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                .build()
                )
                .build();
        return PaymentIntent.create(params);
    }
    public static PaymentIntent confirmPaymentIntent(String paymentIntentId, String paymentMethodId) throws StripeException{
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                .setPaymentMethod(paymentMethodId)
                .build();
        return intent.confirm(params);
    }

    public static class StripeCustomer{
        private String customerId;
        public StripeCustomer(String customerId){
            this.customerId = customerId;
        }
        public String getCustomerId(){
            return customerId;
        }
    }
}
