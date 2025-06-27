package com.teamchallenge.easybuy.services.user;

import com.teamchallenge.easybuy.dto.user.AuthResponseDto;
import com.teamchallenge.easybuy.models.user.EmailConfirmationToken;
import com.teamchallenge.easybuy.models.user.User;
import com.teamchallenge.easybuy.repo.user.EmailConfirmationTokenRepository;
import com.teamchallenge.easybuy.repo.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailConfirmationService {

    private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    @Value("${spring.mail.username}")
    private String username;

    public void send(String emailTo, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(username);
        simpleMailMessage.setTo(emailTo);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        javaMailSender.send(simpleMailMessage);
    }

    public void sendConfirmationEmail(User user, String url) {
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        emailConfirmationTokenRepository.deleteAllByExpiresAtBefore(now);

        EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken();
        emailConfirmationToken.setToken(token);
        emailConfirmationToken.setUser(user);
        emailConfirmationToken.setCreatedAt(now);
        emailConfirmationToken.setExpiresAt(now.plusHours(24));
        emailConfirmationTokenRepository.save(emailConfirmationToken);

        String link = url + "/api/auth/confirm?token=" + token;
        send(user.getEmail(), "Confirm your e-mail address",
                "Please click the link below to confirm your e-mail address: " + link);
    }

    @Transactional
    public void resendConfirmationEmail(String email, String url) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        deleteAllByUser(user);
        sendConfirmationEmail(user, url);
    }

    public AuthResponseDto confirmEmail(String token) {
        EmailConfirmationToken emailConfirmationToken = emailConfirmationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Invalid token"));
        if(emailConfirmationToken.isConfirmed())
            throw new IllegalStateException("Email already confirmed");
        if(emailConfirmationToken.getExpiresAt().isBefore(LocalDateTime.now()))
            throw new IllegalStateException("Token expired");

        emailConfirmationToken.setConfirmed(true);
        emailConfirmationTokenRepository.save(emailConfirmationToken);

        User user = emailConfirmationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        return authenticationService.generateToken(user);
    }

    @Transactional
    public void deleteAllByUser(User user) {
        emailConfirmationTokenRepository.deleteAllByUser(user);
    }
}
