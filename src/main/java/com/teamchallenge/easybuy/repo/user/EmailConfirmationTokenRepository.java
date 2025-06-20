package com.teamchallenge.easybuy.repo.user;

import com.teamchallenge.easybuy.models.user.EmailConfirmationToken;
import com.teamchallenge.easybuy.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Integer> {
    Optional<EmailConfirmationToken> findByToken(String token);

    void deleteAllByUser(User user);
}
