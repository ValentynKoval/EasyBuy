package com.teamchallenge.easybuy.models.shop;

import com.teamchallenge.easybuy.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shops", indexes = {
        @Index(name = "idx_shops_slug", columnList = "slug"),
        @Index(name = "idx_shops_status", columnList = "shop_status"),
        @Index(name = "idx_shops_user", columnList = "owner_id"),
        @Index(name = "idx_shops_featured", columnList = "is_featured")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Base information for a shop.")
public class Shop {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    @Schema(description = "Unique shop ID. Just for database. Read only", example = "e3b0c442-98fc-4629-8b9c-a5de62ed1df1", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @NotNull
    @Column(name = "shop_name", nullable = false)
    @Schema(description = "Name of the shop", example = "MyStore")
    private String shopName;

    @Lob
    @Column(columnDefinition = "TEXT")
    @Schema(description = "Description of the shop", example = "Leading seller of electronics and gadgets.")
    private String shopDescription;

    @NotNull
    @Column(name = "slug", unique = true)
    @Schema(description = "SEO-friendly identifier", example = "mystore")
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)

    @Schema(description = "Owner of the shop (creator). Links to User entity.", example = "123e4567-e89b-12d3-a456-426614174000")
    private User owner;

    @NotNull
    @Column(name = "currency", nullable = false, length = 3)
    @Schema(description = "Currency code (ISO 4217). Important for correct price display and commission calculation",
            example = "UAH")
    private String currency;

    @NotNull
    @Column(name = "business_address", nullable = false)
    @Schema(description = "Full legal business address. Legal requirement, needed for tax documents and delivery.",
            example = "Khreshchatyk St., 1, Kyiv, 01001, Ukraine")
    private String businessAddress;

    @Column(name = "is_email_verified", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the email has been verified. Additional security for important transactions (changing bank details).",
            example = "false")
    private boolean isEmailVerified = false;

    @Column(name = "is_phone_verified", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the phone number has been verified. Additional security for important transactions (changing bank details).",
            example = "false")
    private boolean isPhoneVerified = false;

    @Column(name = "language", length = 5)
    @Builder.Default
    @Schema(description = "Primary language of the shop", example = "uk")
    private String language = "uk";

    @Column(name = "timezone", length = 50)
    @Builder.Default
    @Schema(description = "Timezone of the shop", example = "Europe/Kiev")
    private String timezone = "Europe/Kiev";

    @NotNull
    @Column(name = "shop_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Status of the shop", example = "ACTIVE")
    private ShopStatus shopStatus;

    @NotNull
    @Column(name = "shop_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Type of the shop", example = "INDIVIDUAL")
    private ShopType shopType;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    @Schema(description = "Indicates if the shop is featured", example = "false")
    private boolean isFeatured = false;

    @Column(name = "created_at", updatable = false)
    @Schema(description = "Creation timestamp", example = "2025-07-15T14:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @Column(name = "updated_at")
    @Schema(description = "Last update timestamp", example = "2025-07-15T14:30:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public enum ShopStatus {
        ACTIVE,
        INACTIVE,
        BANNED,
        PENDING,
        REJECTED
    }

    public enum ShopType {
        INDIVIDUAL,
        LEGAL_ENTITY
    }

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "List of manager link records for this shop")
    private List<ShopManager> shopManagers = new ArrayList<>();


}
