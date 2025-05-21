package com.project.airbnb_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotelId;

    private String type;

    @Column(
            name = "base_price",
            nullable = false,
            scale = 6,
            precision = 2
    )
    private BigDecimal basePrice;

    @Column(name = "amenities", columnDefinition = "TEXT[]")
    private String[] amenities;

    @Column(name = "photos", columnDefinition = "TEXT[]")
    private String[] photos;

    @Column(nullable = false)
    private Integer totalCount;

    @Column(nullable = false)
    private Integer capacity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
