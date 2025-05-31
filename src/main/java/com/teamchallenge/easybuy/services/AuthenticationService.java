package com.teamchallenge.easybuy.services;

import com.teamchallenge.easybuy.dto.AuthResponseDto;
import com.teamchallenge.easybuy.dto.LoginRequestDto;
import com.teamchallenge.easybuy.dto.RegisterRequestDto;
import com.teamchallenge.easybuy.models.Role;
import com.teamchallenge.easybuy.models.Token;
import com.teamchallenge.easybuy.models.User;
import com.teamchallenge.easybuy.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Service for handling user registration and authentication.
 * Hashes passwords, authenticates users via AuthenticationManager,
 * and generates access and refresh tokens for authenticated sessions.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public User register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalStateException("The user with this email is already registered");

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        return userRepository.save(User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .isEmailVerified(false)
                .role(Role.valueOf(request.getRole()))
                .build());
    }

    public ResponseEntity<?> authenticate(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()
                    )
            );
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found with email : " + request.getEmail()));
            if (!user.isEmailVerified()) {
                throw new IllegalStateException("Email not confirmed");
            }
            return ResponseEntity.ok(generateToken(user));
        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body( "Incorrect login or password");
        } catch (IllegalStateException ex) {
            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .body(ex.getMessage());
        }
    }

    public AuthResponseDto generateToken(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getRole().name());
        tokenService.createToken(user, refreshToken);
        return new AuthResponseDto(accessToken, refreshToken);
    }

    public AuthResponseDto refresh(String refreshToken) {
        Token token = tokenService.findByToken(refreshToken);
        if (token == null || !tokenService.isValid(token))
            throw new IllegalStateException("Invalid token");

        User user = token.getUser();
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole().name());
        return new AuthResponseDto(accessToken, refreshToken);
    }

    public void logout() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        tokenService.revokedAllTokensByUser(user);
    }
}
