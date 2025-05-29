package com.teamchallenge.easybuy.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Data Transfer Object for Category entity, used in API responses and requests.")
public class CategoryDTO {

    @Schema(description = "Unique ID of the category", example = "d97bb4bc-9f40-4d5f-b68d-4e537e19e8b2")
    private UUID id;

    @NotNull
    @Schema(description = "Name of the category", example = "Men's Jackets")
    private String name;

    @Schema(description = "Parent category ID if this is a subcategory", example = "d97bb4bc-9f40-4d5f-b68d-4e537e19e8b2")
    private UUID parentId;

    @Schema(description = "List of subcategory IDs")
    private List<UUID> subcategoryIds;

    @Schema(description = "List of attributes assigned to this category")
    private List<CategoryAttributeDTO> attributes;

    @Data
    @Schema(description = "Data Transfer Object for CategoryAttribute entity.")
    public static class CategoryAttributeDTO {
        @Schema(description = "Unique ID of the attribute", example = "63f1c7de-89f1-44a3-8288-8bff5f9ad47a")
        private UUID id;

        @Schema(description = "Attribute name", example = "Color")
        private String name;

        @Schema(description = "Type of the attribute", example = "STRING")
        private String type;
    }
}