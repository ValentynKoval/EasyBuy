package com.teamchallenge.easybuy.repo.goods;

import com.teamchallenge.easybuy.models.goods.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface GoodsRepository extends JpaRepository<Goods, UUID>, JpaSpecificationExecutor<Goods> {
    List<Goods> findByCategoryIdIn(Set<UUID> categoryIds);
    boolean existsByArt(String art);
}