package com.teamchallenge.easybuy.models.goods.category;

import com.teamchallenge.easybuy.models.goods.Goods;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Represents the value of a specific attribute for a product (goods).
 */
@Entity
@Table(name = "goods_attributes", indexes = {
        @Index(name = "idx_goods_attributes_goods_id", columnList = "goods_id"),
        @Index(name = "idx_goods_attributes_attribute_id", columnList = "attribute_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Value assigned to a goods' attribute")
public class GoodsAttributeValue {

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    @Schema(description = "Unique ID of the attribute value", example = "9f5f6212-3e18-4a4f-809c-f9fd1fc4e1e9")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id", nullable = false)
    @Schema(description = "Goods that owns this attribute value", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private Goods goods;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    @Schema(description = "Attribute definition this value belongs to", example = "63f1c7de-89f1-44a3-8288-8bff5f9ad47a")
    private CategoryAttribute attribute;

    @Column(nullable = true)
    @Schema(description = "Actual value of the attribute", example = "Red")
    private String value;
}