package com.teamchallenge.easybuy.dto.goods.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Data Transfer Object for GoodsAttributeValue entity.")
public class GoodsAttributeValueDTO {

    @Schema(description = "Unique ID of the attribute value", example = "9f5f6212-3e18-4a4f-809c-f9fd1fc4e1e9")
    private UUID id;

    @NotNull(message = "Goods ID must not be null")
    @Schema(description = "ID of the goods", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    private UUID goodsId;

    @NotNull(message = "Attribute ID must not be null")
    @Schema(description = "ID of the attribute", example = "63f1c7de-89f1-44a3-8288-8bff5f9ad47a")
    private UUID attributeId;

    @Schema(description = "Actual value of the attribute (can be null for optional attributes)", example = "Red")
    private String value;
}