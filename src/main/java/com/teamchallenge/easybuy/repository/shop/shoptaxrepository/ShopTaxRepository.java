package com.teamchallenge.easybuy.repository.shop.shoptaxrepository;

import com.teamchallenge.easybuy.domain.model.shop.ShopTaxInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShopTaxRepository extends JpaRepository<ShopTaxInfo, UUID> {
}
