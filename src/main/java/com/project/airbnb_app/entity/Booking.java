package com.project.airbnb_app.entity;

import com.project.airbnb_app.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotelId;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room roomId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus bookingStatus;

    @Column(nullable = false)
    private LocalDateTime checkInDate;

    private LocalDateTime checkOutDate;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment paymentId;

    @ManyToMany
    @JoinTable(
            name = "booking_guest",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "guest_id")
    )
    private Set<Guest> guest;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

