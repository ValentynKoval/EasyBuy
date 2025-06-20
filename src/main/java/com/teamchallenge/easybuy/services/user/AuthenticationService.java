package com.teamchallenge.easybuy.services.user;

import com.teamchallenge.easybuy.dto.user.AuthResponseDto;
import com.teamchallenge.easybuy.dto.user.ChangePasswordDto;
import com.teamchallenge.easybuy.dto.user.LoginRequestDto;
import com.teamchallenge.easybuy.dto.user.RegisterRequestDto;
import com.teamchallenge.easybuy.models.*;
import com.teamchallenge.easybuy.models.user.Role;
import com.teamchallenge.easybuy.models.user.Token;
import com.teamchallenge.easybuy.models.user.User;
import com.teamchallenge.easybuy.repo.user.UserRepository;
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
import com.teamchallenge.easybuy.models.Customer;

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

    public User register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByEmailAndPhoneNumber(registerRequestDto.getEmail(), registerRequestDto.getPhoneNumber()))
            throw new IllegalStateException("The user with this email or phone number is already registered");

        if (!registerRequestDto.getPassword().equals(registerRequestDto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        User user;
        switch (Role.valueOf(registerRequestDto.getRole())) {
            case CUSTOMER:
                user = Customer.builder()
                    .email(registerRequestDto.getEmail())
                    .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                    .phoneNumber(registerRequestDto.getPhoneNumber())
                    .role(Role.CUSTOMER)
                    .build();
                break;
            case SELLER:
                user = Seller.builder()
                        .email(registerRequestDto.getEmail())
                        .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                        .phoneNumber(registerRequestDto.getPhoneNumber())
                        .role(Role.SELLER)
                        .storeName(registerRequestDto.getStoreName())
                        .build();
                break;
            case MANAGER:
                //Добавить поиск по названию магазина и привязать менеджера к магазину
                user = Manager.builder()
                    .email(registerRequestDto.getEmail())
                    .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                    .phoneNumber(registerRequestDto.getPhoneNumber())
                    .role(Role.MANAGER)
                    .build();
                break;
            default:
                throw new IllegalArgumentException("Invalid role");
        }
        return userRepository.save(user);
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
        tokenService.revokedAllTokensByUser(getUser());
    }

    private User getUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));
    }

    public void changePassword(ChangePasswordDto request) {
        User user = getUser();
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }
}
