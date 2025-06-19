package com.teamchallenge.easybuy.mapper.goods;

import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
import com.teamchallenge.easybuy.models.goods.Goods;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = GoodsImageMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoodsMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(target = "additionalImages", expression = "java(goods.getAdditionalImages() != null ? goods.getAdditionalImages().stream().map(GoodsImageMapper.INSTANCE::toDto).toList() : null)")
    GoodsDTO toDto(Goods goods);

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "additionalImages", ignore = true)
    Goods toEntity(GoodsDTO goodsDTO);
}