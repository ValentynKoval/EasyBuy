package com.teamchallenge.easybuy.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Schema(description = "DTO for representing goods in the catalog, intended for both buyer and seller clients")
public class GoodsDTO {

    @NotNull
    @Schema(description = "Unique identifier for the product", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", accessMode = Schema.AccessMode.READ_ONLY)
    private java.util.UUID id;

    @NotNull
    @Schema(description = "Article number of the product, must be unique", example = "ART-00123")
    private String art;

    @NotNull
    @Schema(description = "Name of the product", example = "Wireless Mouse")
    private String name;


    @Schema(description = "Detailed description of the product", example =
            "A high-precision wireless mouse with ergonomic design.")
    private String description;

    @NotNull
    @Schema(description = "Price of the product", example = "1499.99")
    private BigDecimal price;

    @NotNull
    @Schema(description = "Current stock level", example = "120")
    private Integer stock;

    // TODO: 24.05.2025 Change shopId on Shop name for use on client
    @NotNull
    @Schema(description = "Unique shop ID", example = "1")
    private UUID shopId;

    @Schema(description = "Category to which the product belongs", implementation = CategoryDTO.class)
    private CategoryDTO category;

    @NotNull
    @Schema(description = "Status of the product (e.g., ACTIVE, INACTIVE, ARCHIVED)", example = "ACTIVE")
    private String goodsStatus;

    public enum GoodsStatus {
        ACTIVE,
        INACTIVE,
        ARCHIVED
    }

    @NotNull
    @Schema(description = "Discount status of the product (e.g., NONE, ACTIVE, EXPIRED)", example = "ACTIVE")
    private String discountStatus;

    public enum DiscountStatus {
        NONE,
        ACTIVE,
        EXPIRED
    }

    @Schema(description = "Value of the discount applied to the product", example = "10.00")
    private BigDecimal discountValue;


}