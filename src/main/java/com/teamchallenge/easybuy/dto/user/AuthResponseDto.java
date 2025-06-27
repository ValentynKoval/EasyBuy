package com.teamchallenge.easybuy.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
}
