package com.teamchallenge.easybuy.repo;

import com.teamchallenge.easybuy.models.Token;
import com.teamchallenge.easybuy.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);

    void deleteAllByUser(User user);

    List<Token> findAllByUserAndRevokedFalse(User user);
}
