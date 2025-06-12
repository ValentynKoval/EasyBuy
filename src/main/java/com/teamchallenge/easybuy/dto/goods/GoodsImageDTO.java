package com.teamchallenge.easybuy.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor; // Додайте, якщо немає
import lombok.AllArgsConstructor; // Додайте, якщо немає

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for GoodsImage entity.")
public class GoodsImageDTO {

    @Schema(description = "Unique identifier for the image", example = "a12c56d8-bf7f-4a12-b8ea-2d5d7f4db4a1")
    private UUID id;

     @Schema(description = "URL of the image stored in Cloudinary",
             example = "https://res.cloudinary.com/easybuymarketplace/image.jpg")
    private String imageUrl;

    @Schema(description = "ID of the product this image belongs to", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID goodsId;
}