package com.teamchallenge.easybuy.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "DTO representing the user's address")
public class AddressDto {
    @Schema(description = "User's country of residence", example = "Ukraine")
    private String country;

    @Schema(description = "User's city of residence", example = "Kyiv")
    private String city;

    @Schema(description = "Street and house number of the user", example = "Khreshchatyk St, 22")
    private String street;
}
