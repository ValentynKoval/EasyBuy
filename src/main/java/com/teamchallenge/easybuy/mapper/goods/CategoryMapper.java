package com.teamchallenge.easybuy.mapper.goods;

import com.teamchallenge.easybuy.dto.goods.CategoryDTO;
import com.teamchallenge.easybuy.models.goods.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "path", ignore = true) // Will be calculated in the service
    @Mapping(target = "level", ignore = true) // Will be calculated in the service
    @Mapping(target = "hasSubcategories", ignore = true) // Will be calculated in the service
    CategoryDTO toDto(Category category);
}
