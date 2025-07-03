package com.teamchallenge.easybuy.services.user;

import com.teamchallenge.easybuy.models.user.PasswordResetToken;
import com.teamchallenge.easybuy.models.user.User;
import com.teamchallenge.easybuy.repo.user.PasswordResetTokenRepository;
import com.teamchallenge.easybuy.repo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailConfirmationService emailConfirmationService;
    private final PasswordEncoder passwordEncoder;

    public void sendResetLink(String email, String url) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        LocalDateTime now = LocalDateTime.now();
        passwordResetTokenRepository.deleteAllByExpiresAtBefore(now);

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setCreatedAt(now);
        passwordResetToken.setExpiresAt(now.plusHours(24));

        passwordResetTokenRepository.save(passwordResetToken);

        String link = url + "/api/auth/reset-password?token=" + token;
        emailConfirmationService.send(user.getEmail(), "Password reset",
                "Click on the link to set a new password: " + link);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token).orElseThrow(
                () -> new IllegalStateException("Token not found"));

        if (passwordResetToken.isConfirmed() || passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("The token has expired or has already been used.");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetToken.setConfirmed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Transactional
    void deleteAllByUser(User user) {
        passwordResetTokenRepository.deleteAllByUser(user);
    }
}
