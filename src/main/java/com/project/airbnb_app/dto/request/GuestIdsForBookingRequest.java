package com.project.airbnb_app.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class GuestIdsForBookingRequest {

    List<Long> guestIds;
}
