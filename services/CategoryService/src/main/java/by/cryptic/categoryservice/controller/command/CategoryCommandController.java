package by.cryptic.categoryservice.controller.command;

import by.cryptic.categoryservice.dto.CategoryDTO;
import by.cryptic.categoryservice.service.command.CategoryCreateCommand;
import by.cryptic.categoryservice.service.command.CategoryDeleteCommand;
import by.cryptic.categoryservice.service.command.CategoryUpdateCommand;
import by.cryptic.categoryservice.service.command.handler.CategoryCreateCommandHandler;
import by.cryptic.categoryservice.service.command.handler.CategoryDeleteCommandHandler;
import by.cryptic.categoryservice.service.command.handler.CategoryUpdateCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryCommandController {

    private final CategoryCreateCommandHandler categoryCreateCommandHandler;
    private final CategoryUpdateCommandHandler categoryUpdateCommandHandler;
    private final CategoryDeleteCommandHandler categoryDeleteCommandHandler;

    @PostMapping
    public ResponseEntity<Void> createCategory(
            @RequestBody CategoryCreateCommand categoryDTO) {
        categoryCreateCommandHandler.handle(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategory(@PathVariable UUID categoryId,
            @RequestBody CategoryDTO categoryDTO) {
        categoryUpdateCommandHandler.handle(new CategoryUpdateCommand(categoryId,
                categoryDTO.name(),
                categoryDTO.description()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryDeleteCommandHandler.handle(new CategoryDeleteCommand(id));
        return ResponseEntity.noContent().build();
    }
}