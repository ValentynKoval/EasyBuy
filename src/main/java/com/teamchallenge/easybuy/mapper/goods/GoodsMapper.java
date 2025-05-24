package com.teamchallenge.easybuy.mapper.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
import com.teamchallenge.easybuy.models.goods.Goods;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoodsMapper {
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "shopId", source = "shopId")
    GoodsDTO toDto(Goods goods);

    @Mapping(target = "category", ignore = true) // Category will be set separately if needed
    @Mapping(target = "shopId", source = "shopId")
    Goods toEntity(GoodsDTO goodsDTO);
}