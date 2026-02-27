package com.ismaelebonaventura.home_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "homes")
public class Home {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "home_id", nullable = false, unique = true)
    private Integer homeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private HomeStatus status;

    private String address;

    @Column(name = "street_number")
    private String streetNumber;

    private String city;

    @Column(name = "price_per_kwh")
    private Double pricePerKwh;

    @Column(name = "head_user_id")
    private UUID headUserId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Home(Integer homeId) {
        this.homeId = homeId;
        this.status = HomeStatus.NOT_CONFIGURED;
    }

    /* =====================================================
       DOMAIN LOGIC
       ===================================================== */

    public void configure(String address,
                          String streetNumber,
                          String city,
                          Double pricePerKwh) {

        if (this.status != HomeStatus.NOT_CONFIGURED &&
                this.status != HomeStatus.DISABLED) {
            throw new IllegalStateException(
                    "Home cannot be configured from state: " + status
            );
        }

        if (pricePerKwh == null || pricePerKwh <= 0) {
            throw new IllegalArgumentException(
                    "Price per kWh must be positive"
            );
        }

        this.address = address;
        this.streetNumber = streetNumber;
        this.city = city;
        this.pricePerKwh = pricePerKwh;
        this.status = HomeStatus.CONFIGURED;
    }

    public void disable() {

        if (this.status != HomeStatus.CONFIGURED) {
            throw new IllegalStateException(
                    "Only CONFIGURED homes can be disabled"
            );
        }

        this.status = HomeStatus.DISABLED;
    }

    public void reactivate() {

        if (this.status != HomeStatus.DISABLED) {
            throw new IllegalStateException(
                    "Only DISABLED homes can be reactivated"
            );
        }

        this.status = HomeStatus.CONFIGURED;
    }
}