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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class CheckoutServiceImpl implements CheckoutService {

    private final HotelBookingOrchestratorService hotelBookingOrchestratorService;

    @Override
    public String getCheckoutSession(HotelBooking hotelBooking, String successUrl, String failureUrl) {
        log.debug("Start get checkout session for booking id: {}", hotelBooking.getId());

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.debug("Get the user from security context holder and user id: {}", user.getId());

        Customer customer = null;
        Session session = null;
        try {
            // Crete customer params builder.
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();
            customer = Customer.create(customerCreateParams);

            //Create product data builder.
            SessionCreateParams.LineItem.PriceData.ProductData sessionCreateParamsProductData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(String.format("%s : %s", hotelBooking.getHotel().getName(), hotelBooking.getRoom().getType()))
                    .setDescription(String.format("Booking id: %s", hotelBooking.getId()))
                    .build();

            // Create line item price data builder and append product data.
            SessionCreateParams.LineItem.PriceData sessionCreateParamsPriceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency("INR")
                    .setUnitAmount(hotelBooking.getAmount().multiply(BigDecimal.valueOf(100L)).longValue()) // amount * 100 paisa
                    .setProductData(sessionCreateParamsProductData)
                    .build();

            // Create line item builder and append price data.
            SessionCreateParams.LineItem sessionCratatePatamsLineItem = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(sessionCreateParamsPriceData)
                    .build();

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
}
