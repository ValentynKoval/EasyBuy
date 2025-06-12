    package com.teamchallenge.easybuy.models.goods.category;

    import io.swagger.v3.oas.annotations.media.Schema;

    /**
     * Defines the type of the attribute value.
     */
    public enum AttributeType {
        @Schema(description = "String type attribute", example = "STRING")
        STRING,
        @Schema(description = "Numeric type attribute", example = "NUMBER")
        NUMBER,
        @Schema(description = "Boolean type attribute", example = "BOOLEAN")
        BOOLEAN,
        @Schema(description = "Enumerated type attribute", example = "ENUM")
        ENUM
    }