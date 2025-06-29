package by.cryptic.productservice.controller.query;

import by.cryptic.productservice.dto.CategoryDTO;
import by.cryptic.productservice.service.query.category.CategoryGetAllQuery;
import by.cryptic.productservice.service.query.category.handler.CategoryGetAllQueryHandler;
import by.cryptic.productservice.service.query.category.handler.CategoryGetByIdQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories/")
@RequiredArgsConstructor
public class CategoryQueryController {

    private final CategoryGetAllQueryHandler categoryGetAllQueryHandler;
    private final CategoryGetByIdQueryHandler categoryGetByIdQueryHandler;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllReviews() {
        return ResponseEntity.ok(categoryGetAllQueryHandler.handle(new CategoryGetAllQuery()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getReviewById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryGetByIdQueryHandler.handle(id));
    }
}