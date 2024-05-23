package Stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import java.io.FileNotFoundException;

public class StripePaymentProcessor {
    public StripePaymentProcessor() throws FileNotFoundException {
        String stripeApiKey = StripeConfig.getApiKey();
        if(stripeApiKey != null){
            Stripe.apiKey = stripeApiKey;
        }
        else{
            throw new RuntimeException("Stripe API key not found");
        }
    }

    public PaymentIntent createPaymentIntent(double amount) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                                            .setAmount((long) (amount * 100))
                                            .setCurrency("usd")
                                            .setPaymentMethod("pm_card_visa")
                                            .setAutomaticPaymentMethods(
                                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                                    .setEnabled(true)
                                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                                    .build()
                                            )
                                            .build();
        return PaymentIntent.create(params);
    }
}
