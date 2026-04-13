package com.teamchallenge.easybuy.product.repository;

import com.teamchallenge.easybuy.product.entity.category.GoodsAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface GoodsAttributeValueRepository extends JpaRepository<GoodsAttributeValue, UUID>, JpaSpecificationExecutor<GoodsAttributeValue> {
}