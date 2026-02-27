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
        name = "home_members",
        indexes = {
                @Index(name = "idx_home_members_home", columnList = "home_id"),
                @Index(name = "idx_home_members_member", columnList = "member_user_id", unique = true)
        }
)
public class HomeMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "home_id", nullable = false)
    private Integer homeId;

    @Column(name = "member_user_id", nullable = false, unique = true)
    private UUID memberUserId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public HomeMember(Integer homeId, UUID memberUserId) {
        this.homeId = homeId;
        this.memberUserId = memberUserId;
    }
}