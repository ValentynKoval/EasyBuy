package com.teamchallenge.easybuy.controllers.user;

import com.teamchallenge.easybuy.dto.user.*;
import com.teamchallenge.easybuy.models.user.User;
import com.teamchallenge.easybuy.services.user.AuthenticationService;
import com.teamchallenge.easybuy.services.user.EmailConfirmationService;
import com.teamchallenge.easybuy.services.user.PasswordResetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final EmailConfirmationService emailConfirmationService;
    private final PasswordResetService passwordResetService;

    @Operation(summary = "User registration", description = "Sends a link to confirm your email.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successful registration"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Registration failed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Input validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Validation error",
                                    value = "{\"email\": \"Incorrect email format\", \"password\": \"Password cannot be empty\"}"
                            )
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequestDto, HttpServletRequest request) {
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
        } catch (ResponseStatusException ex) {
            return ResponseEntity
                    .status(ex.getStatusCode())
                    .body(ex.getReason());
        }
    }

    @Operation(summary = "Email confirmation", description = "Confirms email using token from email")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Email verified successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Email confirmation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
    @GetMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestParam("token") String token) {
        try {
            return ResponseEntity.ok(emailConfirmationService.confirmEmail(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.GONE).body(e.getMessage());
        }
    }

    @Operation(summary = "Resend link to email", description = "Resends the email link to the user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sending successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            )
    })
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

    @Operation(summary = "User authorization", description = "Returns JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful authorization",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Incorrect login or password",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Authorization unsuccessful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Input validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Validation error",
                                    value = "{\"email\": \"Incorrect email format\", \"password\": \"Password cannot be empty\"}"
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @Operation(summary = "Access token update", description = "Returns new access and old refresh tokens")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful authorization",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token is invalid",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Input validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "Validation error",
                                    value = "{\"refreshToken\": \"Refresh token cannot be empty\"}"
                            )
                    )
            )
    })
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

    @Operation(summary = "Log out of account", description = "Logs out of user account")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Log out successful"
            )
    })
    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        authenticationService.logout();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change password in account", description = "Change of account's password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Changed success"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Passwords do not match"
            )
    })

    @PutMapping("/change_password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto request) {
        try {
            authenticationService.changePassword(request);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
        } catch (UsernameNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody EmailRequestDto email, HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
        passwordResetService.sendResetLink(email.getEmail(), baseUrl);
        return ResponseEntity.ok("A reset link has been sent to your email address, if one exists.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @Valid @RequestBody ChangePasswordDto request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }
        passwordResetService.resetPassword(token, request.getPassword());
        return ResponseEntity.ok("Your password has been reset.");
    }
}
