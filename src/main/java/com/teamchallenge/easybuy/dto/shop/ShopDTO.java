package com.teamchallenge.easybuy.dto.shop;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for Goods entity, used in API responses and requests.")
public class ShopDTO {

    @Schema(description = "Unique identifier for the shop. Read only", example = "e3b0c442-98fc-4629-8b9c-a5de62ed1df1"
            , accessMode = Schema.AccessMode.READ_ONLY)
    private UUID shopId;

    @NotBlank
    @Size(min = 1, max = 100)
    @Schema(
            description = "Name of the shop. Max Length = 100.",
            example = "MyStore",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 100, minLength = 1)
    private String shopName;

    @NotBlank
    @Size(max = 1000)
    @Schema(
            description = "Store description (up to 1000 characters).",
            example = "Leading seller of Shoe and Dress.",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 1000
    )
    private String shopDescription;

    @NotNull
    @Schema(
            description = "Status of the shop",
            example = "ACTIVE",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Shop.ShopStatus shopStatus;

    @Schema(
            description = "Flag: Display store as preferred/favorite.",
            example = "false"
    )
    private Boolean isFeatured = false;


    @NotNull
    @Schema(
            description = "Seller ID. In the DTO, we use only the ID, without the entity reference.",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID sellerId;

    @DecimalMin(value = "0.0000")
    @DecimalMax(value = "1.0000")
    @Schema(
            description = "Commission rate taken by marketplace (e.g., 0.05 for 5%). " +
                    "The commission cannot be negative or greater than 1 (100%) ",
            example = "0.05")
    private BigDecimal commissionRate;


    @Schema(
            description = "The store's peak activity time. Typically set by the server.",
            example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Instant lastActivityAt;


    @Schema(
            description = "Flag: The store is verified. Usually set by the server.",
            example = "false",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Boolean isVerified;


    @Schema(
            description = "Type of store, e.g. Sale, Manufacturer, Reseller",
            example = "Producer"
    )
    private Shop.ShopType shopType;

    @Schema(description = "Public link to the shop's logo image")
    private String shopLogoUrl;

    @Schema(description = "URL-friendly identifier for the shop", example = "coffee-world-kyiv")
    private String slug;

    @Schema(
            description = "Reason for rejection of the store",
            example = "Store is not suitable for selling goods")
    private String rejectionReason;

    @Schema(
            description = "Internal notes from admins/moderators regarding the store. Not visible to customers.",
            example = "Re-verification required in 3 months.",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String moderatorNotes;

    @Schema(
            description = "When the store was last moderated. Read-only.",
            example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Instant lastModeratedAt;


    @Schema(
            description = "The ID of the user (admin) who moderated the store. Read-only.",
            example = "00000000-0000-0000-0000-000000000000",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private UUID moderatedByUserId;

    @Schema(description = "Advanced SEO settings for search engine adaptation")
    private ShopSeoSettingsDTO seoSettings;

    @NotBlank
    @Size(max = 5)
    @Schema(
            description = "ISO 4217 currency code",
            example = "UAH",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 5
    )
    private String currency;

    @NotBlank
    @Size(max = 50)
    @Schema(
            description = "Shop time zone (IANA).",
            example = "Europe/Kyiv",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 50
    )
    private String language;

    @NotBlank
    @Size(max = 50)
    @Schema(
            description = "Store Time Zone (IANA)).",
            example = "Europe/Kyiv",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 50
    )
    private String timezone;

    @Schema(
            description = "Optimistic locking version (used by the server).",
            example = "0",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private long version;

    @Schema(
            description = "When the store is created (audit).",
            example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Instant createdAt;

    @Schema(
            description = "When the store is updated (audit).",
            example = "2025-05-28T11:21:00Z",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Instant updatedAt;


    public enum ShopStatus {
        ACTIVE,
        INACTIVE,
        PENDING,
        BANNED,
        REJECTED
    }

    public enum ShopType {
        RETAILER,
        PRODUCER,
        RESELLER,
        OTHER
    }

}
