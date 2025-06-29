package by.cryptic.productservice.controller.command;

import by.cryptic.productservice.service.command.category.CategoryCreateCommand;
import by.cryptic.productservice.service.command.category.CategoryDeleteCommand;
import by.cryptic.productservice.service.command.category.CategoryUpdateCommand;
import by.cryptic.productservice.service.command.category.handler.CategoryCreateCommandHandler;
import by.cryptic.productservice.service.command.category.handler.CategoryDeleteCommandHandler;
import by.cryptic.productservice.service.command.category.handler.CategoryUpdateCommandHandler;
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
    public ResponseEntity<Void> createReview(
            @RequestBody CategoryCreateCommand categoryDTO) {
        categoryCreateCommandHandler.handle(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateReview(
            @RequestBody CategoryUpdateCommand categoryDTO) {
        categoryUpdateCommandHandler.handle(categoryDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id) {
        categoryDeleteCommandHandler.handle(new CategoryDeleteCommand(id));
        return ResponseEntity.noContent().build();
    }
}