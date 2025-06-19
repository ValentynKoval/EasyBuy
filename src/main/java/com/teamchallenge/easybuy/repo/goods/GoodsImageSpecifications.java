package com.teamchallenge.easybuy.repo.goods;

import com.teamchallenge.easybuy.models.goods.GoodsImage;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class GoodsImageSpecifications {

    public static Specification<GoodsImage> hasGoodsId(UUID goodsId) {
        return (root, query, cb) -> goodsId == null ? null : cb.equal(root.get("goods").get("id"), goodsId);
    }
}