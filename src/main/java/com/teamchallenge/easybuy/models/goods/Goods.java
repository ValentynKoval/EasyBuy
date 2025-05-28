package com.teamchallenge.easybuy.models.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "goods")
@Schema(description = "Goods entity used to display goods in the catalog.")
public class Goods {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    @Schema(description = "Unique identifier for the product", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;

    @NotNull
    @Column(name = "art", nullable = false)
    @Schema(description = "Article number of the product", example = "ART-00123")
    private String art;

    @NotNull
    @Column(name = "name", nullable = false)
    @Schema(description = "Name of the product", example = "Wireless Mouse")
    private String name;

    @Lob
    @Column(name = "description")
    @Schema(description = "Detailed description of the product")
    private String description;

    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @Schema(description = "Price of the product", example = "1499.99")
    private BigDecimal price;

    @NotNull
    @Column(name = "mainImageUrl", nullable = false)
    @Schema(description = "Main image URL of the product")
    private String mainImageUrl;

    @NotNull
    @Column(name = "stock", nullable = false)
    @Schema(description = "Current stock level", example = "120")
    private Integer stock;

    @Column(name = "reviewsCount")
    private Integer reviewsCount;

    @NotNull
    @Column(name = "shopId", nullable = false)
    private UUID shopId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    @NotNull
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "goodsStatus", nullable = false)
    private GoodsStatus goodsStatus;

    public enum GoodsStatus {
        ACTIVE,
        INACTIVE,
        ARCHIVED
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "discountStatus", nullable = false)
    private DiscountStatus discountStatus;

    public enum DiscountStatus {
        NONE,
        ACTIVE,
        EXPIRED
    }

    @Column(name = "discountValue", precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    /**
     * List of additional images associated with this product.
     * Cascade operations are enabled. Orphan removal ensures deleted images are removed from DB.
     */
    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoodsImage> additionalImages = new ArrayList<>();
}
