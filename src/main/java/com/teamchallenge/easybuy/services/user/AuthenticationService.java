package com.teamchallenge.easybuy.services.user;

import com.teamchallenge.easybuy.dto.user.AuthResponseDto;
import com.teamchallenge.easybuy.dto.user.ChangePasswordDto;
import com.teamchallenge.easybuy.dto.user.LoginRequestDto;
import com.teamchallenge.easybuy.dto.user.RegisterRequestDto;
import com.teamchallenge.easybuy.models.user.*;
import com.teamchallenge.easybuy.repo.user.UserRepository;
import com.teamchallenge.easybuy.services.goods.image.CloudinaryImageService;
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

import java.io.IOException;

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
    private final CloudinaryImageService cloudinaryImageService;

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
                        .avatarUrl(cloudinaryImageService.generateAvatarUrl(registerRequestDto.getEmail()))
                        .build();
                break;
            case SELLER:
                // todo add the creation of a store and assign a name to it
                user = Seller.builder()
                        .email(registerRequestDto.getEmail())
                        .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                        .phoneNumber(registerRequestDto.getPhoneNumber())
                        .role(Role.SELLER)
                        .avatarUrl(cloudinaryImageService.generateAvatarUrl(registerRequestDto.getStoreName()))
                        .build();
                break;
            case MANAGER:
                // todo add a search by store name and link the manager to the store
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

    public void updateAvatarUrl(String avatarUrl) {
        User user = getUser();
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
    }

    public void deleteAvatarUrl() throws IOException {
        User user = getUser();
        String avatarUrl = user.getAvatarUrl();
        String publicId = cloudinaryImageService.extractPublicIdFromUrl(avatarUrl);
        if (publicId != null) {
            cloudinaryImageService.deleteImage(publicId);
        }
        // todo make it so that depending on how the role is, the avatar is generated from the corresponding name
        if (user.getRole().equals(Role.CUSTOMER)) {
            user.setAvatarUrl(cloudinaryImageService.generateAvatarUrl(user.getEmail()));
        } else {
            user.setAvatarUrl(null);
        }
        userRepository.save(user);
    }
}
