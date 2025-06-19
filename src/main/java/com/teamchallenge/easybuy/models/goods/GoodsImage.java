package com.teamchallenge.easybuy.models.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "goods_images", indexes = {
        @Index(name = "idx_goods_images_goods_id", columnList = "goods_id")
})
@Schema(description = "Represents an additional image associated with a product.")
public class GoodsImage {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    @Schema(description = "Unique identifier for the image", example = "a12c56d8-bf7f-4a12-b8ea-2d5d7f4db4a1")
    private UUID id;

    @NotNull
    @Column(name = "image_url", nullable = false)
    @Schema(description = "URL of the image", example = "https://example.com/images/product123_2.jpg")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "goods_id", nullable = false)
    @NotNull
    @Schema(description = "The product this image belongs to", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private Goods goods;
}