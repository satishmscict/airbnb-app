package com.project.airbnb_app.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripePaymentGatewayConfiguration {

    StripePaymentGatewayConfiguration(@Value("${stripe.secretKey}") String secretKey) {
        Stripe.apiKey = secretKey;
    }
}
