package com.teamchallenge.easybuy.mapper.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
import com.teamchallenge.easybuy.models.goods.Goods;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoodsMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "goodsStatus", target = "goodsStatus", expression = "java(goods.getGoodsStatus().toString())")
    @Mapping(source = "discountStatus", target = "discountStatus", expression = "java(goods.getDiscountStatus().toString())")
    @Mapping(target = "additionalImageUrls", expression = "java(goods.getAdditionalImages().stream().map(GoodsImage::getImageUrl).toList())")
    GoodsDTO toDto(Goods goods);

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "goodsStatus", target = "goodsStatus", expression = "java(com.teamchallenge.easybuy.models.goods.Goods.GoodsStatus.valueOf(goodsDTO.getGoodsStatus()))")
    @Mapping(source = "discountStatus", target = "discountStatus", expression = "java(com.teamchallenge.easybuy.models.goods.Goods.DiscountStatus.valueOf(goodsDTO.getDiscountStatus()))")
    Goods toEntity(GoodsDTO goodsDTO);
}