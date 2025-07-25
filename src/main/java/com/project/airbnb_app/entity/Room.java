package com.project.airbnb_app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnore
    private Hotel hotel;

    @Column(nullable = false)
    private String type;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal basePrice;

    @Column(name = "amenities", columnDefinition = "TEXT[]")
    private String[] amenities;

    @Column(name = "photos", columnDefinition = "TEXT[]")
    private String[] photos;

    @Column(nullable = false)
    private Integer totalRoomCount;

    @Column(nullable = false)
    private Integer roomCapacity;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
