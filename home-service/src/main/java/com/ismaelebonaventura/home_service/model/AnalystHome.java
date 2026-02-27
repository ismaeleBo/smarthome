package com.ismaelebonaventura.home_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "analyst_homes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_analyst_home", columnNames = {"analyst_user_id", "home_id"})
        }
)
public class AnalystHome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "analyst_user_id", nullable = false)
    private UUID analystUserId;

    @Column(name = "home_id", nullable = false)
    private Integer homeId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AnalystHome(UUID analystUserId, Integer homeId) {
        this.analystUserId = analystUserId;
        this.homeId = homeId;
    }
}