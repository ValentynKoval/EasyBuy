package com.teamchallenge.easybuy.repo.goods;

import com.teamchallenge.easybuy.models.goods.GoodsImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface GoodsImageRepository extends JpaRepository<GoodsImage, UUID>, JpaSpecificationExecutor<GoodsImage> {
}