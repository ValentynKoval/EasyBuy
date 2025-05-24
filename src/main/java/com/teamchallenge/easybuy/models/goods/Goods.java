package com.teamchallenge.easybuy.models.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

//**
// * * Represents a product (advertisement) in the store's catalog.
// * This entity contains information about a product's name, price, stock,
// * category, shop, and discount status.
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "goods")
@Schema(description = "Goods entity used to display goods in the catalog.")
public class Goods {

    /**
     * The unique identifier for the product in database.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    @Schema(description = "Unique identifier for the product", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    /**
     * The article number (art) of the product, used for uniquely identifying products beyond just the ID.
     * This field is required and must be unique.
     * User can change art.
     */
    @NotNull
    @Column(name = "art", nullable = false, unique = true)
    @Schema(description = "Article number of the product, must be unique", example = "ART-00123")
    private String art;


    @NotNull
    @Column(name = "name", nullable = false)
    @Schema(description = "Name of the product", example = "Wireless Mouse")
    private String name;


    @Lob
    @Column(name = "description")
    @Schema(description = "Detailed description of the product", example = "A high-precision wireless mouse with ergonomic design.")
    private String description;


    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @Schema(description = "Price of the product", example = "1499.99", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    /**
     * The current stock level of the product.
     * Represents how many items of this product are available.
     */
    @NotNull
    @Column(name = "stock", nullable = false)
    @Schema(description = "Current stock level", example = "120", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer stock;


    @NotNull
    @Column(name = "shopId", nullable = false)
    @Schema(description = "Unique shop ID")
    private UUID shopId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    @NotNull
    @Schema(description = "Category to which the product belongs", implementation = Category.class)
    private Category category;


    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "goodsStatus", nullable = false)
    @Schema(description = "Status of the product (e.g., ACTIVE, INACTIVE, ARCHIVED)", example = "ACTIVE")
    private GoodsStatus goodsStatus;

    public enum GoodsStatus {
        ACTIVE,
        INACTIVE,
        ARCHIVED
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "discountStatus", nullable = false)
    @Schema(description = "Discount status of the product (e.g., NONE, ACTIVE, EXPIRED)", example = "ACTIVE")
    private DiscountStatus discountStatus;


    public enum DiscountStatus {
        NONE,
        ACTIVE,
        EXPIRED
    }


    @Column(name = "discountValue", precision = 10, scale = 2)
    @Schema(description = "Value of the discount applied to the product", example = "10.00")
    private BigDecimal discountValue;


}
