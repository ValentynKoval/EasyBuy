package com.teamchallenge.easybuy.dto.shop;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for advanced SEO settings to ensure Google search visibility and indexing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "SEO configuration for shop discovery and adaptation")
public class ShopSeoSettingsDTO {

    @Schema(description = "Meta title for search engines", example = "Best Coffee Shop in Kyiv")
    private String metaTitle;

    @Schema(description = "Meta description for SEO snippets", example = "Organic coffee beans delivered to your door.")
    private String metaDescription;

    @Schema(description = "Keywords for indexing", example = "coffee, organic, delivery, kyiv")
    private String metaKeywords;

    @Schema(description = "Canonical URL to avoid duplicate content")
    private String canonicalUrl;
}