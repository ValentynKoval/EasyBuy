package com.teamchallenge.easybuy.dto.goods.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "Data Transfer Object for Category entity, used in API responses and requests.")
public class CategoryDTO {

    @Schema(description = "Unique ID of the category", example = "d97bb4bc-9f40-4d5f-b68d-4e537e19e8b2")
    private UUID id;

    @NotBlank(message = "Category name must not be blank")
    @Schema(description = "Name of the category", example = "Men's Jackets")
    private String name;

    @Schema(description = "Parent category ID if this category is a subcategory (null for top-level categories)", example = "d97bb4bc-9f40-4d5f-b68d-4e537e19e8b2")
    private UUID parentId;

    @Schema(description = "List of subcategory IDs (empty if there are no subcategories)")
    private List<UUID> subcategoryIds;

    @Schema(description = "List of attributes assigned to this category (empty if none)")
    private List<CategoryAttributeDTO> attributes;
}