package com.teamchallenge.easybuy.mapper.goods;

import com.teamchallenge.easybuy.dto.goods.CategoryDTO;
import com.teamchallenge.easybuy.models.goods.category.Category;
import com.teamchallenge.easybuy.models.goods.category.CategoryAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "parentCategory.id", target = "parentId")
    @Mapping(target = "subcategoryIds", expression = "java(category.getSubcategories().stream().map(Category::getId).toList())")
    @Mapping(target = "attributes", expression = "java(category.getAttributes().stream().map(this::toAttributeDto).toList())")
    CategoryDTO toDto(Category category);

    @Mapping(source = "parentId", target = "parentCategory.id")
    Category toEntity(CategoryDTO categoryDTO);

    @Mapping(source = "type", target = "type", expression = "java(attribute.getType().toString())")
    CategoryDTO.CategoryAttributeDTO toAttributeDto(CategoryAttribute attribute);
}