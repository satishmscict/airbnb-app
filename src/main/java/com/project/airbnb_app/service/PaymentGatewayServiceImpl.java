package com.project.airbnb_app.service;

import com.project.airbnb_app.entity.HotelBooking;
import com.project.airbnb_app.entity.User;
import com.project.airbnb_app.entity.enums.BookingStatus;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    private static final String CURRENCY_INR = "INR";
    private static final int CURRENCY_UNIT = 100;
    private static final String STRIPE_EVENT_CHECKOUT_SESSION_COMPLETED = "checkout.session.completed";

    @Value("${stripe.frontEndBaseUrl}")
    private String paymentGatewayRedirectBaseUrl;

    private final AppUserDomainService appUserDomainService;
    private final HotelBookingService hotelBookingService;
    private final HotelBookingDomainService hotelBookingDomainService;
    private final RoomInventoryService roomInventoryService;

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
            hotelBookingService.saveBooking(hotelBooking);

        } catch (StripeException e) {
            log.error("Failed to create the payment session. Error info: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return session.getUrl();
    }

    @Override
    public void processRefundAmount(HotelBooking hotelBooking) {
        try {
            Session session = Session.retrieve(hotelBooking.getPaymentSessionId());
            RefundCreateParams refundCreateParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();

            Refund.create(refundCreateParams);
        } catch (StripeException e) {
            log.error("Stripe refund failed with the error message: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Transactional
    @Override
    public void capturePaymentEvent(Event event) {
        if (event.getType().equals(STRIPE_EVENT_CHECKOUT_SESSION_COMPLETED)) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session == null) return;

            HotelBooking hotelBooking = hotelBookingDomainService.findByPaymentSessionId(session.getId());

            hotelBooking.setBookingStatus(BookingStatus.CONFIRMED);
            hotelBookingService.saveBooking(hotelBooking);

            // To avoid concurrent modification.
            roomInventoryService.findAndLockInventoryForModification(
                    hotelBooking.getRoom().getId(),
                    hotelBooking.getCheckInDate().toLocalDate(),
                    hotelBooking.getCheckOutDate().toLocalDate(),
                    hotelBooking.getRoomsCount()
            );

            roomInventoryService.confirmBooking(
                    hotelBooking.getRoom().getId(),
                    hotelBooking.getCheckInDate().toLocalDate(),
                    hotelBooking.getCheckOutDate().toLocalDate(),
                    hotelBooking.getRoomsCount()
            );

            log.info("Successfully completed booking for the id: {}", hotelBooking.getId());
        } else {
            log.debug("Unhandled event type : {}", event.getType());
        }
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
