package com.teamchallenge.easybuy.models.shop;

import com.teamchallenge.easybuy.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shop_managers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"shop_id", "user_id"})
}, indexes = {
        @Index(name = "idx_shop_managers_shop_id", columnList = "shop_id"),
        @Index(name = "idx_shop_managers_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Links a user to a shop as a manager.")
public class ShopManager {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    @Schema(description = "Unique identifier for the shop manager record", example = "a1b2c3d4-e5f6-7890-abcd-1234567890ef")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Schema(description = "Shop that the user manages")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Schema(description = "User who is assigned as a manager to the shop")
    private User user;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    @Schema(description = "Date when the user was assigned as a manager", example = "2025-07-15T14:30:00Z")
    private Instant assignedAt;

    @PrePersist
    protected void onCreate() {
        this.assignedAt = Instant.now();
    }
}