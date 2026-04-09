package com.teamchallenge.easybuy.repository.shop.shopanalytics;

import com.teamchallenge.easybuy.domain.model.shop.ShopAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShopAnalyticsRepository extends JpaRepository<ShopAnalytics, UUID> {

    List<ShopAnalytics> findByDeadShopTrueOrderByInactiveDaysDesc();
}

