package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelBooking;
import com.project.airbnb_app.entity.User;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private static final String CURRENCY_INR = "INR";
    private static final int CURRENCY_UNIT = 100;
    private final AppUserDomainService appUserDomainService;
    @Value("${stripe.frontEndBaseUrl}")
    private String paymentGatewayRedirectBaseUrl;
    private final HotelBookingOrchestratorService hotelBookingOrchestratorService;

    @Override
    public String createCheckoutSession(HotelBooking hotelBooking) {
        log.debug("Start get checkout session for booking id: {}", hotelBooking.getId());

        User user = appUserDomainService.getCurrentUser();
        log.debug("Get the user from security context holder and user id: {}", user.getId());

        String successUrl = buildSuccessUrl(
                hotelBooking.getAmount().toString(),
                hotelBooking.getHotel().getName(),
                hotelBooking.getRoom().getType(),
                user.getName());

        String failureUrl = buildFailureUrl(
                hotelBooking.getAmount().toString(),
                hotelBooking.getHotel().getName(),
                hotelBooking.getRoom().getType(),
                user.getName()
        );
        
        Session session = null;
        try {
            // Crete customer params builder.
            Customer customer = createStripeCustomer(user);

            // Create session params builder.
            SessionCreateParams.LineItem sessionCratatePatamsLineItem = createStripeLineItem(hotelBooking);

            // Bind all the session related parameters and preparing session.
            SessionCreateParams sessionCreateParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(sessionCratatePatamsLineItem)
                    .build();

            session = Session.create(sessionCreateParams);

            hotelBooking.setPaymentSessionId(session.getId());
            hotelBookingOrchestratorService.save(hotelBooking);

        } catch (StripeException e) {
            log.error("Failed to create the payment session. Error info: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return session.getUrl();
    }

    private String buildFailureUrl(String amount, String hotelName, String roomType, String username) {
        return String.format("%s/payments/payment-cancel.html?user=%s&hotel=%s&room=%s&amount=%s",
                paymentGatewayRedirectBaseUrl,
                username,
                hotelName,
                roomType,
                amount
        );
    }

    private String buildSuccessUrl(String amount, String hotelName, String roomType, String username) {
        return String.format("%s/payments/payment-success.html?user=%s&hotel=%s&room=%s&amount=%s&success=true",
                paymentGatewayRedirectBaseUrl,
                username,
                hotelName,
                roomType,
                amount
        );
    }

    private Customer createStripeCustomer(User user) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(user.getName())
                .setEmail(user.getEmail())
                .build();
        return Customer.create(params);
    }

    private SessionCreateParams.LineItem createStripeLineItem(HotelBooking hotelBooking) {
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(String.format("%s : %s", hotelBooking.getHotel().getName(), hotelBooking.getRoom().getType()))
                        .setDescription(String.format("Booking id: %s", hotelBooking.getId()))
                        .build();

        Long unitAmount = hotelBooking.getAmount()
                .multiply(BigDecimal.valueOf(CURRENCY_UNIT))
                .longValue();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(CURRENCY_INR)
                        .setUnitAmount(unitAmount)
                        .setProductData(productData)
                        .build();

        return SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();
    }
}
