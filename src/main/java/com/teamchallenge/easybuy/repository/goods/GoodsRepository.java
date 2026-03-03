package com.teamchallenge.easybuy.repository.goods;

import com.teamchallenge.easybuy.domain.model.goods.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface GoodsRepository extends JpaRepository<Goods, UUID>, JpaSpecificationExecutor<Goods> {
    boolean existsByArt(String art);
}