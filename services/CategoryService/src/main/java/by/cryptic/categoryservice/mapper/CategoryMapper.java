package by.cryptic.categoryservice.mapper;

import by.cryptic.categoryservice.dto.CategoryDTO;
import by.cryptic.categoryservice.model.read.CategoryView;
import by.cryptic.categoryservice.model.write.Category;
import by.cryptic.categoryservice.service.command.CategoryUpdateCommand;
import by.cryptic.utils.event.category.CategoryUpdatedEvent;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public static CategoryDTO toDto(CategoryView categoryView) {
        if (categoryView == null) {
            return null;
        }
        return new CategoryDTO(categoryView.getName(), categoryView.getDescription());
    }

    public static CategoryDTO toDto(Category category) {
        if (category == null) {
            return null;
        }
        return new CategoryDTO(category.getName(), category.getDescription());
    }

    public static void updateEntity(Category category, CategoryUpdateCommand categoryDTO) {
        if (category == null || categoryDTO == null) return;

        if (categoryDTO.name() != null) {
            category.setName(categoryDTO.name());
        }
        if (categoryDTO.description() != null) {
            category.setDescription(categoryDTO.description());
        }
    }

    public static void updateView(CategoryView categoryView, CategoryUpdatedEvent event) {
        if (event.getName() != null) {
            categoryView.setName(event.getName());
        }
        if (event.getDescription() != null) {
            categoryView.setDescription(event.getDescription());
        }
    }
}
