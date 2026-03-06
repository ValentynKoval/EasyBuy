package com.teamchallenge.easybuy.repository.user.seller;

import com.teamchallenge.easybuy.domain.model.user.Seller;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface SellerRepository extends JpaRepository<Seller, UUID> {
}
