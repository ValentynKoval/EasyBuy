package com.teamchallenge.easybuy.dto.goods;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Schema(description = "DTO for representing a category in the catalog, including hierarchical structure, path, level, " +
        "and subcategories indicator for filtering")
public class CategoryDTO {
    @Schema(description = "Unique identifier of the category", example = "f47ac10b")
    private UUID id;

    @Schema(description = "Name of the category", example = "Electronics")
    private String name;

    @Schema(description = "Detailed description of the category", example = "All kinds of electronic devices " +
            "and accessories")
    private String description;

    @Schema(description = "Parent category, if this is a subcategory")
    private CategoryDTO parent;

    @Schema(description = "Subcategories belonging to this category")
    private Set<CategoryDTO> subcategories;

    @Schema(description = "Whether the category is active and visible", example = "true")
    private boolean enabled;

    @Schema(description = "Full hierarchical path of the category, e.g., 'Electronics > Mice > Wireless Mice'",
            example = "Electronics > Mice > Wireless Mice")
    private String path;

    @Schema(description = "Depth level of the category in the hierarchy (0 for root, 1 for first-level children, etc.)",
            example = "2")
    private int level;

    @Schema(description = "Indicates whether the category has subcategories", example = "true")
    private boolean hasSubcategories;
}