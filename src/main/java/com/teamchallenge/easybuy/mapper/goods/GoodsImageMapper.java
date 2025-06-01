package com.teamchallenge.easybuy.mapper.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsImageDTO;
import com.teamchallenge.easybuy.models.goods.GoodsImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoodsImageMapper {

    @Mapping(source = "goods.id", target = "goodsId")
    GoodsImageDTO toDto(GoodsImage image);

    @Mapping(source = "goodsId", target = "goods.id")
    GoodsImage toEntity(GoodsImageDTO dto);
}