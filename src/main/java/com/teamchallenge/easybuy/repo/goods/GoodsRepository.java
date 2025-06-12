package com.teamchallenge.easybuy.repo.goods;

import com.teamchallenge.easybuy.models.goods.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface GoodsRepository extends JpaRepository<Goods, UUID>, JpaSpecificationExecutor<Goods> {
    boolean existsByArt(String art);
}