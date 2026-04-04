package com.teamchallenge.easybuy.mapper.goods.category;

import com.teamchallenge.easybuy.domain.model.goods.Goods;
import com.teamchallenge.easybuy.domain.model.goods.category.Category;
import com.teamchallenge.easybuy.domain.model.shop.Shop;
import com.teamchallenge.easybuy.dto.goods.GoodsDTO;
import com.teamchallenge.easybuy.dto.goods.category.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = CategoryAttributeMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(source = "parentCategory.id", target = "parentId")
    @Mapping(target = "subcategoryIds", expression = "java(category.getSubcategories() " +
            "!= null ? category.getSubcategories().stream().map(Category::getId).toList() : null)")
    @Mapping(target = "attributes", expression = "java(category.getAttributes() " +
            "!= null ? category.getAttributes().stream().map(CategoryAttributeMapper.INSTANCE::toDto).toList() : null)")
    CategoryDTO toDto(Category category);

    @Mapping(source = "parentId", target = "parentCategory.id")
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);

    @Mapping(source = "shop.shopId", target = "shopId")
    GoodsDTO toDto(Goods goods);

    @Mapping(source = "shopId", target = "shop.shopId")
    Goods toEntity(GoodsDTO dto);

    // Shop -> UUID
    default UUID map(Shop shop) {
        if (shop == null) return null;
        return shop.getShopId();
    }

    //  UUID -> Shop
    default Shop map(UUID id) {
        if (id == null) return null;
        Shop shop = new Shop();
        shop.setShopId(id);
        return shop;
    }
}