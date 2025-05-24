package com.teamchallenge.easybuy.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "DTO for representing goods in the catalog, intended for both buyer and seller clients")
public class GoodsDTO {
    @Schema(description = "Unique identifier for the product", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", accessMode = Schema.AccessMode.READ_ONLY)
    private java.util.UUID id;

    @Schema(description = "Article number of the product, must be unique", example = "ART-00123", required = true)
    private String art;

    @Schema(description = "Name of the product", example = "Wireless Mouse", required = true)
    private String name;

    @Schema(description = "Detailed description of the product", example = "A high-precision wireless mouse with ergonomic design.")
    private String description;

    @Schema(description = "Price of the product", example = "1499.99", required = true)
    private BigDecimal price;

    @Schema(description = "Current stock level", example = "120", required = true)
    private Integer stock;

    // TODO: 24.05.2025 Change shopId on Shop name for use on client
    @Schema(description = "Unique shop ID", example = "1", required = true)
    private UUID shopId;

    @Schema(description = "Category to which the product belongs", implementation = CategoryDTO.class)
    private CategoryDTO category;

    @Schema(description = "Status of the product (e.g., ACTIVE, INACTIVE, ARCHIVED)", example = "ACTIVE", required = true)
    private String goodsStatus;

    public enum GoodsStatus {
        ACTIVE,
        INACTIVE,
        ARCHIVED
    }

    @Schema(description = "Discount status of the product (e.g., NONE, ACTIVE, EXPIRED)", example = "ACTIVE", required = true)
    private String discountStatus;

    public enum DiscountStatus {
        NONE,
        ACTIVE,
        EXPIRED
    }

    @Schema(description = "Value of the discount applied to the product", example = "10.00")
    private BigDecimal discountValue;


}