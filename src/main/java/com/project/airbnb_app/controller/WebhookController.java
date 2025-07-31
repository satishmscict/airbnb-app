package com.project.airbnb_app.controller;

import com.project.airbnb_app.service.PaymentGatewayService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webhook")
@Slf4j
@Tag(name = "Webhook")
public class WebhookController {

    private final PaymentGatewayService paymentGatewayService;

    @Value("${stripe.webhook.secretKey}")
    private String webHookSecret;

    @PostMapping("/payment")
    public ResponseEntity<Void> paymentWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String stripeSignatureHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, stripeSignatureHeader, webHookSecret);
            paymentGatewayService.capturePaymentEvent(event);

            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException e) {
            log.error("Payment webhook failed with the error : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
