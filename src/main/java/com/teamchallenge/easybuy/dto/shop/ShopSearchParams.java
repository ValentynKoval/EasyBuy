package com.teamchallenge.easybuy.dto.shop;

import com.teamchallenge.easybuy.domain.model.shop.Shop;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Criteria for searching and filtering shops")
public class ShopSearchParams {

    @Schema(description = "Unique identifier of the shop", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID shopId;

    @Schema(description = "Shop name (supports partial or exact match)", example = "Tech Haven")
    private String shopName;

    @Schema(description = "Keywords found in the shop description", example = "electronics and gadgets")
    private String shopDescription;

    @Schema(description = "Current operational status of the shop", example = "ACTIVE")
    private Shop.ShopStatus shopStatus;

    @Schema(description = "Filter by featured status", example = "true")
    private Boolean isFeatured;

    @Schema(description = "Identifier of the seller who owns the shop", example = "a1b2c3d4-e5f6-g7h8-i9j0")
    private UUID sellerId;

    @Schema(description = "Identifier of the moderator who reviewed the shop", example = "b2c3d4e5-f6g7-h8i9-j0k1")
    private UUID moderatedByUserId;

    @Schema(description = "Unique URL-friendly identifier (slug)", example = "tech-haven-official")
    private String slug;

    @Schema(description = "Filter shops created after this timestamp", example = "2024-01-01T00:00:00Z")
    private Instant minCreatedAt;

    @Schema(description = "Filter shops created before this timestamp", example = "2026-12-31T23:59:59Z")
    private Instant maxCreatedAt;

    @Schema(description = "Filter shops updated after this timestamp", example = "2024-06-01T12:00:00Z")
    private Instant minUpdatedAt;

    @Schema(description = "Filter shops updated before this timestamp", example = "2026-06-01T12:00:00Z")
    private Instant maxUpdatedAt;

    @Schema(description = "Universal search term for name and description combined", example = "premium")
    private String keyword;

    @Schema(description = "ID подкатегории для фильтрации магазинов по товарам в этой подкатегории", example = "d97bb4bc-9f40-4d5f-b68d-4e537e19e8b2")
    private UUID subcategoryId;

}