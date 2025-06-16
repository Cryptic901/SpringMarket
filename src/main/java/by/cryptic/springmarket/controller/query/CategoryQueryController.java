package by.cryptic.springmarket.controller.query;

import by.cryptic.springmarket.service.query.CategoryDTO;
import by.cryptic.springmarket.service.query.CategoryGetAllQuery;
import by.cryptic.springmarket.service.query.handler.category.CategoryGetAllQueryHandler;
import by.cryptic.springmarket.service.query.handler.category.CategoryGetByIdQueryHandler;
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
