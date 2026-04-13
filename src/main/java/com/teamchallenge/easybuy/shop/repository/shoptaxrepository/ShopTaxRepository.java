package com.teamchallenge.easybuy.shop.repository.shoptaxrepository;

import com.teamchallenge.easybuy.shop.entity.ShopTaxInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShopTaxRepository extends JpaRepository<ShopTaxInfo, UUID> {
}
