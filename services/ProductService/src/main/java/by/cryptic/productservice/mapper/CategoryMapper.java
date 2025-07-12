package by.cryptic.productservice.mapper;

import by.cryptic.productservice.dto.CategoryDTO;
import by.cryptic.utils.event.category.CategoryUpdatedEvent;
import by.cryptic.productservice.model.read.CategoryView;
import by.cryptic.productservice.model.write.Category;
import by.cryptic.productservice.service.command.category.CategoryUpdateCommand;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

   public CategoryDTO toDto(CategoryView categoryView) {
       if (categoryView == null) {
           return null;
       }
       return new CategoryDTO(categoryView.getName(), categoryView.getDescription());
   }

    public void updateEntity(Category category, CategoryUpdateCommand categoryDTO) {
        if (category == null || categoryDTO == null) return;

        if (categoryDTO.name() != null) {
            category.setName(categoryDTO.name());
        }
        if (categoryDTO.description() != null) {
            category.setDescription(categoryDTO.description());
        }
    }

   public void updateView(CategoryView categoryView, CategoryUpdatedEvent event) {
       if (event.getName() != null) {
           categoryView.setName(event.getName());
       }
       if (event.getDescription() != null) {
           categoryView.setDescription(event.getDescription());
       }
   }
}
