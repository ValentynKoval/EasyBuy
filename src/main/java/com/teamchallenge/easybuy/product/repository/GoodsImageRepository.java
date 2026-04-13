package com.teamchallenge.easybuy.product.repository;

import com.teamchallenge.easybuy.product.entity.GoodsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface GoodsImageRepository extends JpaRepository<GoodsImage, UUID>, JpaSpecificationExecutor<GoodsImage> {
}