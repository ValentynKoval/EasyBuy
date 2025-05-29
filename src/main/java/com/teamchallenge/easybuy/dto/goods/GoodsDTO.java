package com.teamchallenge.easybuy.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Data Transfer Object for Goods entity, used in API responses and requests.")
public class GoodsDTO {

    @Schema(description = "Unique identifier for the product", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID id;

    @NotNull
    @Schema(description = "Article number of the product", example = "ART-00123")
    private String art;

    @NotNull
    @Schema(description = "Name of the product", example = "Wireless Mouse")
    private String name;

    @Schema(description = "Detailed description of the product", example = "A high-precision wireless mouse with ergonomic design.")
    private String description;

    @NotNull
    @Schema(description = "Price of the product", example = "1499.99")
    private BigDecimal price;

    @NotNull
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid URL format")
    @Schema(description = "Main image URL of the product", example = "https://example.com/images/product123.jpg")
    private String mainImageUrl;

    @NotNull
    @Schema(description = "Current stock level", example = "120")
    private Integer stock;

    @Schema(description = "Number of reviews for the product", example = "45")
    private Integer reviewsCount;

    @NotNull
    @Schema(description = "ID of the shop owning the product", example = "a1b2c3d4-5678-90ef-ghij-klmnopqrstuv")
    private UUID shopId;

    @Schema(description = "Category ID this product belongs to", example = "d97bb4bc-9f40-4d5f-b68d-4e537e19e8b2")
    private UUID categoryId;

    @NotNull
    @Schema(description = "Status of the product", example = "ACTIVE")
    private String goodsStatus;

    @NotNull
    @Schema(description = "Discount status of the product", example = "NONE")
    private String discountStatus;

    @Schema(description = "Discount value if applicable", example = "10.00")
    private BigDecimal discountValue;

    @Schema(description = "Average rating of the product", example = "4")
    private Integer rating;

    @Schema(description = "SEO-friendly slug for the product", example = "wireless-mouse-123")
    private String slug;

    @Schema(description = "Meta title for SEO", example = "Wireless Mouse - Best Price")
    private String metaTitle;

    @Schema(description = "Meta description for SEO", example = "High-quality wireless mouse at the best price.")
    private String metaDescription;

    @Schema(description = "Timestamp when the product was created", example = "2025-05-28T11:21:00Z")
    private Instant createdAt;

    @Schema(description = "Timestamp when the product was last updated", example = "2025-05-28T11:21:00Z")
    private Instant updatedAt;

    @Schema(description = "List of additional image URLs for the product")
    private List<String> additionalImageUrls;
}