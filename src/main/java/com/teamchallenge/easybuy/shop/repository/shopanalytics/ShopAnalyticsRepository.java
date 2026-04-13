package com.teamchallenge.easybuy.shop.repository.shopanalytics;

import com.teamchallenge.easybuy.shop.entity.ShopAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShopAnalyticsRepository extends JpaRepository<ShopAnalytics, UUID> {

    List<ShopAnalytics> findByDeadShopTrueOrderByInactiveDaysDesc();
}

