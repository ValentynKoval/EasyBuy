package com.teamchallenge.easybuy.repository.shop.shopmoderationhistory;

import com.teamchallenge.easybuy.domain.model.shop.ShopModerationHistory;
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

