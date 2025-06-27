package com.teamchallenge.easybuy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with profile data and user address")
public class CustomerProfileResponseDto {
    @Schema(description = "Basic user information")
    private CustomerProfileDto profile;

    @Schema(description = "User address")
    private AddressDto address;
}
