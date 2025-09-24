package com.teamchallenge.easybuy.models.shop;

import com.teamchallenge.easybuy.models.goods.Goods;
import com.teamchallenge.easybuy.models.user.Manager;
import com.teamchallenge.easybuy.models.user.Seller;
import com.teamchallenge.easybuy.models.shop.ShopContactInfo;
import com.teamchallenge.easybuy.models.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "shops", indexes = {

        @Index(name = "idx_shops_slug", columnList = "slug"),
        @Index(name = "idx_shops_status", columnList = "shop_status"),
        @Index(name = "idx_shops_featured", columnList = "is_featured"),
      //   todo Check
//        @Index(name = "idx_shop_managerId", columnList = "manager_id"),
        @Index(name = "idx_shop_sellerId", columnList = "seller_id"),
        @Index(name = "idx_shop_shopId", columnList = "shop_id"),
        @Index(name = "idx_shop_name", columnList = "shop_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Base information for a shop.")
public class Shop {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "shop_id", nullable = false, updatable = false)
    @Schema(description = "Unique shop ID. Just for database. Read only", example = "e3b0c442-98fc-4629-8b9c-a5de62ed1df1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private UUID shopId;

    @NotNull
    @Column(name = "shop_name", nullable = false, unique = true, length = 100)
    @Schema(description = "Name of the shop. Max Length = 100.", example = "MyStore",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 100, minLength = 1)
    private String shopName;

    @Lob
    @NotNull
    @Size(max = 1000)
    @Column(name = "shop_description", nullable = false, length = 1000)
    @Schema(description = "Description of the shop. Max Length = 1000.", example = "Leading seller of Shoe and Dress.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String shopDescription;

    @NotNull
    @Column(name = "slug", unique = true)
    @Schema(description = "SEO-friendly identifier", example = "mystore")
    private String slug;

    @NotNull
    @Column(name = "shop_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Status of the shop", example = "ACTIVE")
    private ShopStatus shopStatus;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    @Schema(description = "Indicates if the shop is featured or recommended on the main page.", example = "false")
    private boolean isFeatured = false;


    public enum ShopStatus {
        @Schema(description = "Store is active and available for buyers")
        ACTIVE,
        @Schema(description = "Store is inactive and temporarily unavailable")
        INACTIVE,
        @Schema(description = "Store is awaiting administrator review before activation")
        PENDING,
        @Schema(description = "Store is blocked due to policy violations")
        BANNED,
        @Schema(description = "Store is rejected due to policy violations")
        REJECTED
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    @Schema(description = "Owner of the shop (creator). Links to Seller entity.",
            example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Seller seller;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "List of goods available in your store")
    private List<Goods> goods = new ArrayList<>();

    @Column(name = "commission_rate", precision = 5, scale = 4)
    @Schema(description = "Commission rate taken by marketplace (e.g., 0.05 for 5%)", example = "0.05")
    private BigDecimal commissionRate;

    @Column(name = "last_activity_at")
    @Schema(description = "Last activity timestamp for the shop (for example, remaining delivery of goods, processing orders)",
            example = "2025-06-27T10:00:00Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant lastActivityAt;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    @Schema(description = "Whether the store is verified by the marketplace (Like ID, TAX, Billing)", example = "false")
    private boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "shop_type")
    @Schema(description = "Type of store, e.g. Sale, Manufacturer, Reseller", example = "Producer")
    private ShopType shopType;

    public enum ShopType {
        @Schema(description = "A store that sells goods to other users")
        RETAILER,
        @Schema(description = "A store that manufactures goods itself")
        PRODUCER,
        @Schema(description = "A store that resells goods from other manufacturers")
        RESELLER,
        @Schema(description = "Another, unclassified type of store")
        OTHER
    }

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    @Schema(description = "Reason for rejection of the store", example = "Store is not suitable for selling goods")
    private String rejectionReason;

    @Lob
    @Column(name = "moderator_notes")
    @Schema(description = "Internal notes from admins/moderators regarding the store. Not visible to customers.",
            example = "Re-verification required in 3 months.")
    private String moderatorNotes;

    @Column(name = "last_moderated_at")
    @Schema(description = "Last time the store was moderated by an admin. Read only.", example = "2025-06-27T10:00:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant lastModeratedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderated_by_user_id")
    @Schema(description = "Admin user who moderated the store. Read only.",
            example = "123e4567-e89b-12d3-a456-426614174000",
            accessMode = Schema.AccessMode.READ_ONLY)
    private User moderatedByUser;

    @NotBlank
    @Column(name = "currency", nullable = false, length = 5)
    @Builder.Default
    @Size(max = 5, message = "Currency code must not exceed 5 characters")
    @Schema(description = "Store currency code ISO 4217", example = "UAH", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency = "UAH";

    @NotBlank
    @Column(name = "language", nullable = false, length = 5)
    @Builder.Default
    @Size(max = 5, message = "Language code must not exceed 5 characters")
    @Schema(description = "Primary language of the shop (ISO 639-1)", example = "uk",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String language = "uk";

    @NotBlank
    @Column(name = "timezone", nullable = false, length = 50)
    @Builder.Default
    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    @Schema(description = "Timezone of the shop (IANA Time Zone Database)", example = "Europe/Kiev",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String timezone = "Europe/Kiev";


    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "Shop contact information")
    private List<ShopContactInfo> shopContactInfo = new ArrayList<>();

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "History of moderation actions for this shop")
    private List<ShopModerationHistory> moderationHistory = new ArrayList<>();

    //todo map to db schema`s
//    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    @Schema(description = "Shop Analytics")
//    private ShopAnalytics analytics;
//
//    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    @Schema(description = "Shop Billing Information")
//    private ShopBillingInfo billingInfo;
//
//    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    @Schema(description = "Shop Tax Information")
//    private ShopTaxInfo taxInfo;

    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "SEO settings for the shop")
    private ShopSeoSettings seoSettings;

    //todo connect to schema shopManagers
    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @Schema(description = "List of manager link records for this shop")
    private List<Manager> shopManagers = new ArrayList<>();

    // --- Fields for autofill and maintenance ---

    @Column(name = "created_at", updatable = false)
    @Schema(description = "The time the store was created. Set automatically on first save.", example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;

    @Column(name = "updated_at")
    @Schema(description = "The time the store was last updated. Set automatically each time the entity is updated.",
            example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;


    //- is called automatically before the first preservation of the essence
    //- sets the current time in the createdAt field
    //- sets the default values if they are not installed
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.currency == null) this.currency = "UAH";
        if (this.language == null) this.language = "uk";
        if (this.timezone == null) this.timezone = "Europe/Kiev";
    }

    //- is called automatically before each renewal of essence
    //- updatedAt and Lastactivity is updated by the current time
    @PreUpdate
    @Schema(hidden = true)
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        this.lastActivityAt = Instant.now();
    }
}