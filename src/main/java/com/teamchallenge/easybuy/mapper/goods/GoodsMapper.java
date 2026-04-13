package com.teamchallenge.easybuy.mapper.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
import com.teamchallenge.easybuy.domain.model.goods.Goods;
import com.teamchallenge.easybuy.domain.model.shop.Shop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring",
        uses = {GoodsImageMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoodsMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "shop.shopId", target = "shopId")
    GoodsDTO toDto(Goods goods);

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "additionalImages", ignore = true)
    @Mapping(source = "shopId", target = "shop", qualifiedByName = "uuidToShop")
    Goods toEntity(GoodsDTO goodsDTO);

    @Named("uuidToShop")
    default Shop uuidToShop(UUID id) {
        if (id == null) return null;
        Shop shop = new Shop();
        shop.setShopId(id);
        return shop;
    }
}