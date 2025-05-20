package com.teamchallenge.easybuy.controllers;

import com.teamchallenge.easybuy.dto.AuthResponseDto;
import com.teamchallenge.easybuy.dto.LoginRequestDto;
import com.teamchallenge.easybuy.dto.RefreshTokenRequestDto;
import com.teamchallenge.easybuy.dto.RegisterRequestDto;
import com.teamchallenge.easybuy.models.User;
import com.teamchallenge.easybuy.services.AuthenticationService;
import com.teamchallenge.easybuy.services.EmailConfirmationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final EmailConfirmationService emailConfirmationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto, HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
        try {
            User user = authenticationService.register(registerRequestDto);
            emailConfirmationService.sendConfirmationEmail(user, baseUrl);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam("token") String token) {
        try {
            return ResponseEntity.ok(emailConfirmationService.confirmEmail(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
        }
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<?> resendConfirmation(@RequestParam String email, HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
        emailConfirmationService.resendConfirmationEmail(email, baseUrl);
        return ResponseEntity.ok("The new token has been sent to the email");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        try {
            AuthResponseDto authResponseDto = authenticationService.refresh(request.getRefreshToken());
            return ResponseEntity.ok(authResponseDto);
        } catch (IllegalStateException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        authenticationService.logout();
        return ResponseEntity.ok().build();
    }
}
