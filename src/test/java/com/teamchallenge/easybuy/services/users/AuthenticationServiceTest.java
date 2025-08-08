package com.teamchallenge.easybuy.services.users;

import com.teamchallenge.easybuy.dto.user.AuthResponseDto;
import com.teamchallenge.easybuy.dto.user.ChangePasswordDto;
import com.teamchallenge.easybuy.models.user.Customer;
import com.teamchallenge.easybuy.models.user.Role;
import com.teamchallenge.easybuy.models.user.Token;
import com.teamchallenge.easybuy.models.user.User;
import com.teamchallenge.easybuy.repo.user.UserRepository;
import com.teamchallenge.easybuy.services.goods.image.CloudinaryImageService;
import com.teamchallenge.easybuy.services.user.AuthenticationService;
import com.teamchallenge.easybuy.services.user.JwtService;
import com.teamchallenge.easybuy.services.user.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private CloudinaryImageService cloudinaryImageService;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = Customer.builder()
                .email("test@example.com")
                .role(Role.CUSTOMER)
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("generateToken should return access and refresh tokens")
    void generateToken_ShouldReturnTokens() {
        String access = "access";
        String refresh = "refresh";

        when(jwtService.generateAccessToken(user.getEmail(), user.getRole().name())).thenReturn(access);
        when(jwtService.generateRefreshToken(user.getEmail(), user.getRole().name())).thenReturn(refresh);

        AuthResponseDto result = authenticationService.generateToken(user);

        assertEquals(access, result.getAccessToken());
        assertEquals(refresh, result.getRefreshToken());
        verify(tokenService).createToken(user, refresh);
    }

    @Test
    @DisplayName("refresh should generate new access token when token is valid")
    void refresh_ShouldReturnNewAсcessToken_WhenTokenValid() {
        String refresh = "refresh";
        Token token = Token.builder()
                .token(refresh)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(60))
                .revoked(false)
                .build();

        when(tokenService.findByToken(refresh)).thenReturn(token);
        when(tokenService.isValid(token)).thenReturn(true);
        when(jwtService.generateAccessToken(user.getEmail(), user.getRole().name())).thenReturn("newAccess");

        AuthResponseDto result = authenticationService.refresh(refresh);

        assertEquals("newAccess", result.getAccessToken());
        assertEquals(refresh, result.getRefreshToken());
    }

    @Test
    @DisplayName("refresh should throw IllegalStateException when token is invalid")
    void refresh_ShouldThrowException_WhenTokenInvalid() {
        String refresh = "refresh";
        Token token = Token.builder()
                .token(refresh)
                .user(user)
                .expiryDate(Instant.now().minusSeconds(60))
                .revoked(true)
                .build();

        when(tokenService.findByToken(refresh)).thenReturn(token);
        when(tokenService.isValid(token)).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> authenticationService.refresh(refresh));
    }

    @Test
    @DisplayName("logout should revoke all tokens for current user")
    void logout_ShouldRevokeTokensForCurrentUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getEmail());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        authenticationService.logout();

        verify(tokenService).revokedAllTokensByUser(user);
    }

    @Test
    @DisplayName("changePassword should update password when confirmation matches")
    void changePassword_ShouldUpdatePassword_WhenConfirmed() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getEmail());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass")).thenReturn("encoded");

        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setPassword("newPass");
        changePasswordDto.setConfirmPassword("newPass");

        authenticationService.changePassword(changePasswordDto);

        assertEquals("encoded", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("changePassword should throw exception when passwords do not matchs")
    void changePassword_ShouldThrowException_WhenPasswordsMismatch() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(user.getEmail());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ChangePasswordDto changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setPassword("newPass");
        changePasswordDto.setConfirmPassword("otherPass");

        assertThrows(ResponseStatusException.class, () -> authenticationService.changePassword(changePasswordDto));
        verify(userRepository, never()).save(any());
    }
}
