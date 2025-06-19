package com.teamchallenge.easybuy.repo.goods;

import com.teamchallenge.easybuy.models.goods.category.GoodsAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface GoodsAttributeValueRepository extends JpaRepository<GoodsAttributeValue, UUID>, JpaSpecificationExecutor<GoodsAttributeValue> {
}