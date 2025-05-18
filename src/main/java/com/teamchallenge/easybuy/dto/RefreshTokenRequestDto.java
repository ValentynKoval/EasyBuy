package com.teamchallenge.easybuy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequestDto {
    @NotBlank
    private String refreshToken;
}
