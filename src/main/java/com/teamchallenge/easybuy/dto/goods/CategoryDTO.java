package com.teamchallenge.easybuy.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Schema(description = "DTO for representing a category in the catalog, including hierarchical structure, path, level, " +
        "and subcategories indicator for filtering")
public class CategoryDTO {
    @Schema(description = "Unique identifier of the category", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
    @NotNull
    private UUID id;

    @Schema(description = "Name of the category", example = "Electronics")
    @NotNull
    private String name;

    @Schema(description = "Detailed description of the category", example = "All kinds of electronic devices and accessories")
    private String description;

    @Schema(description = "ID of the parent category, if this is a subcategory", example = "a1b2c3d4-5678-90ef-ghij-klmnopqrstuv")
    private UUID parentId;

    @Schema(description = "IDs of subcategories belonging to this category")
    private Set<UUID> subcategoryIds;

    @Schema(description = "Whether the category is active and visible", example = "true")
    private boolean enabled;

    @Schema(description = "Full hierarchical path of the category, e.g., 'Electronics > Mice > Wireless Mice'",
            example = "Electronics > Mice > Wireless Mice")
    private String path;

    @Schema(description = "Depth level of the category in the hierarchy (0 for root, 1 for first-level children, etc.)",
            example = "2")
    @Min(0)
    private int level;

    @Schema(description = "Indicates whether the category has subcategories", example = "true")
    private boolean hasSubcategories;
}