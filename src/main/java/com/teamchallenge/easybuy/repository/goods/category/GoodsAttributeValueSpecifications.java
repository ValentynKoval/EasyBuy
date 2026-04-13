package com.teamchallenge.easybuy.repository.goods.category;

import com.teamchallenge.easybuy.domain.model.goods.category.GoodsAttributeValue;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class GoodsAttributeValueSpecifications {

    public static Specification<GoodsAttributeValue> hasGoodsId(UUID goodsId) {
        return (root, query, cb) -> goodsId == null ? null : cb.equal(root.get("goods").get("id"), goodsId);
    }
}