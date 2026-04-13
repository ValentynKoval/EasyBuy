package com.teamchallenge.easybuy.auth.repository;

import com.teamchallenge.easybuy.auth.entity.EmailConfirmationToken;
import com.teamchallenge.easybuy.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Integer> {
    Optional<EmailConfirmationToken> findByToken(String token);

    void deleteAllByUser(User user);

    void deleteAllByExpiresAtBefore(LocalDateTime now);
}
