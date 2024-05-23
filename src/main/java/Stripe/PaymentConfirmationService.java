package Stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;

import java.io.FileNotFoundException;

public class PaymentConfirmationService {

    public PaymentIntent confirmPaymentIntent(String paymentIntentId, String paymentMethodId) throws StripeException, FileNotFoundException {
        String key = null;
        key = StripeConfig.getApiKey();
        if(key != null){
            Stripe.apiKey = key;
        }

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        PaymentIntentConfirmParams params =
                PaymentIntentConfirmParams.builder()
                        .setPaymentMethod(paymentMethodId)
                        .build();

        return paymentIntent.confirm(params);
    }
}
