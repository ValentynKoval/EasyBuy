package com.teamchallenge.easybuy.repository.shop.shopseosettings;

import com.teamchallenge.easybuy.domain.model.shop.ShopSeoSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShopSeoSettingsRepository extends JpaRepository<ShopSeoSettings, UUID> {
}

