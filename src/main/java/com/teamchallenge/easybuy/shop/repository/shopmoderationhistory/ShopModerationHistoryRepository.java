package com.teamchallenge.easybuy.shop.repository.shopmoderationhistory;

import com.teamchallenge.easybuy.shop.entity.ShopModerationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopModerationHistoryRepository extends JpaRepository<ShopModerationHistory, UUID> {

    List<ShopModerationHistory> findByShop_ShopIdOrderByCreatedAtDesc(UUID shopId);

    Optional<ShopModerationHistory> findByModerationHistoryIdAndShop_ShopId(UUID moderationHistoryId, UUID shopId);
}

