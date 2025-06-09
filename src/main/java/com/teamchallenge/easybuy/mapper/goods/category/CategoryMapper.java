package com.teamchallenge.easybuy.mapper.goods.category;

import com.teamchallenge.easybuy.dto.goods.category.CategoryAttributeDTO;
import com.teamchallenge.easybuy.dto.goods.category.CategoryDTO;
import com.teamchallenge.easybuy.models.goods.category.Category;
import com.teamchallenge.easybuy.models.goods.category.CategoryAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

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
}