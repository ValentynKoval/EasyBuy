package com.teamchallenge.easybuy.services;

import com.teamchallenge.easybuy.dto.AuthResponseDto;
import com.teamchallenge.easybuy.dto.LoginRequestDto;
import com.teamchallenge.easybuy.dto.RegisterRequestDto;
import com.teamchallenge.easybuy.exceptions.UserAlreadyExistsException;
import com.teamchallenge.easybuy.models.Role;
import com.teamchallenge.easybuy.models.User;
import com.teamchallenge.easybuy.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public void register(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        userRepository.save(User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .createdAt(LocalDateTime.now())
                .isEmailVerified(false)
                .role(Role.CUSTOMER)
                .build());
    }

    public AuthResponseDto authenticate(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email : " + request.getEmail()));

        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getRole().name());
        tokenService.createToken(user, refreshToken);

        return new AuthResponseDto(accessToken, refreshToken);
    }
}
