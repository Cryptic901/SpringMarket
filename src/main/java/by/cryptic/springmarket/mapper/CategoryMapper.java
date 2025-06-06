package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.event.category.CategoryUpdatedEvent;
import by.cryptic.springmarket.model.read.CategoryView;
import by.cryptic.springmarket.model.write.Category;
import by.cryptic.springmarket.service.command.CategoryCreateCommand;
import by.cryptic.springmarket.service.command.CategoryUpdateCommand;
import by.cryptic.springmarket.service.query.CategoryDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryCreateCommand categoryDTO);

    CategoryCreateCommand toDto(Category categoryView);

    CategoryView toEntity(CategoryDTO categoryDTO);

    CategoryDTO toDto(CategoryView categoryView);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Category category, CategoryUpdateCommand categoryDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateView(@MappingTarget CategoryView categoryView, CategoryUpdatedEvent event);
}
