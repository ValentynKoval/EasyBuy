package com.teamchallenge.easybuy.domain.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Preferred contact method")
public enum ContactMethod {

    @Schema(description = "Contact via email")
    EMAIL,

    @Schema(description = "Contact via phone")
    PHONE,

    @Schema(description = "Contact via website")
    WEBSITE,

    @Schema(description = "Contact via social media")
    SOCIAL_MEDIA
}