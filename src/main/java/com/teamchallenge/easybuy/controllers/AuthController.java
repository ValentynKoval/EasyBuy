package com.teamchallenge.easybuy.controllers;

import com.teamchallenge.easybuy.dto.AuthResponseDto;
import com.teamchallenge.easybuy.dto.LoginRequestDto;
import com.teamchallenge.easybuy.dto.RegisterRequestDto;
import com.teamchallenge.easybuy.models.Token;
import com.teamchallenge.easybuy.models.User;
import com.teamchallenge.easybuy.services.AuthenticationService;
import com.teamchallenge.easybuy.services.JwtService;
import com.teamchallenge.easybuy.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
        authenticationService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        AuthResponseDto authResponseDto = authenticationService.authenticate(request);
        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestParam String refreshToken) {
        Token token = isTokenVerified(refreshToken);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = token.getUser();
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponseDto(accessToken, refreshToken));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam String refreshToken) {
        Token token = isTokenVerified(refreshToken);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        tokenService.revokeToken(token);
        return ResponseEntity.ok().build();
    }

    private Token isTokenVerified(String refreshToken) {
        if (refreshToken == null) {
            return null;
        }
        Token token = tokenService.findByToken(refreshToken);
        if (token == null && !tokenService.isValid(token)) {
            return null;
        }
        return token;
    }
}
