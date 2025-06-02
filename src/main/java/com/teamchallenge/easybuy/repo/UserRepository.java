package com.teamchallenge.easybuy.repo;

import com.teamchallenge.easybuy.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByEmailAndPhoneNumber(String email, String phoneNumber);
}
