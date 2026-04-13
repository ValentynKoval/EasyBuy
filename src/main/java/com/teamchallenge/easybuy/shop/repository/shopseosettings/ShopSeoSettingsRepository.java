package com.teamchallenge.easybuy.shop.repository.shopseosettings;

import com.teamchallenge.easybuy.shop.entity.ShopSeoSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShopSeoSettingsRepository extends JpaRepository<ShopSeoSettings, UUID> {
}

