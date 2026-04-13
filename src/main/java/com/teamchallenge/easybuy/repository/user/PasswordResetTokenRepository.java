package com.teamchallenge.easybuy.repository.user;

import com.teamchallenge.easybuy.domain.model.user.PasswordResetToken;
import com.teamchallenge.easybuy.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteAllByExpiresAtBefore(LocalDateTime expiresAt);

    void deleteAllByUser(User user);
}
