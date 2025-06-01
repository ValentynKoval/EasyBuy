package com.teamchallenge.easybuy.mapper.goods.category;

import com.teamchallenge.easybuy.dto.goods.category.CategoryAttributeDTO;
import com.teamchallenge.easybuy.dto.goods.category.CategoryDTO;
import com.teamchallenge.easybuy.models.goods.category.Category;
import com.teamchallenge.easybuy.models.goods.category.CategoryAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = CategoryAttributeMapper.class)
public interface CategoryMapper {

    @Mapping(source = "parentCategory.id", target = "parentId")
    @Mapping(target = "subcategoryIds", expression = "java(category.getSubcategories() != null ? category.getSubcategories().stream().map(Category::getId).toList() : null)")
    @Mapping(target = "attributes", expression = "java(category.getAttributes() != null ? category.getAttributes().stream().map(this::toAttributeDto).toList() : null)")
    CategoryDTO toDto(Category category);

    @Mapping(source = "parentId", target = "parentCategory.id")
    Category toEntity(CategoryDTO categoryDTO);

    @Mapping(source = "type", target = "type", qualifiedByName = "mapTypeToString")
    CategoryAttributeDTO toAttributeDto(CategoryAttribute attribute);

    @Named("mapTypeToString")
    default String mapTypeToString(com.teamchallenge.easybuy.models.goods.category.AttributeType type) {
        return type != null ? type.toString() : null;
    }
}