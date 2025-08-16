package by.cryptic.categoryservice.controller.query;

import by.cryptic.categoryservice.dto.CategoryDTO;
import by.cryptic.categoryservice.service.query.CategoryGetAllQuery;
import by.cryptic.categoryservice.service.query.handler.CategoryGetAllQueryHandler;
import by.cryptic.categoryservice.service.query.handler.CategoryGetByIdQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryQueryController {

    private final CategoryGetAllQueryHandler categoryGetAllQueryHandler;
    private final CategoryGetByIdQueryHandler categoryGetByIdQueryHandler;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryGetAllQueryHandler.handle(new CategoryGetAllQuery()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryGetByIdQueryHandler.handle(id));
    }
}