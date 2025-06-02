package com.teamchallenge.easybuy.repo;

import com.teamchallenge.easybuy.models.Shop;
import com.teamchallenge.easybuy.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {
    Shop findByUser(User user);
}
