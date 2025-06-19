package com.teamchallenge.easybuy.dto.goods.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Data Transfer Object for CategoryAttribute entity.")
public class CategoryAttributeDTO {

    @Schema(description = "Unique ID of the attribute", example = "63f1c7de-89f1-44a3-8288-8bff5f9ad47a")
    private UUID id;

    @NotBlank(message = "Attribute name must not be blank")
    @Schema(description = "Attribute name", example = "Color")
    private String name;

    @NotNull(message = "Attribute type must not be null")
    @Schema(description = "Type of the attribute", example = "STRING", allowableValues = {"STRING", "NUMBER", "BOOLEAN", "ENUM"})
    private String type;

    @NotNull(message = "Category ID must not be null")
    @Schema(description = "Category ID this attribute belongs to", example = "d97bb4bc-9f40-4d5f-b68d-4e537e19e8b2")
    private UUID categoryId;
}