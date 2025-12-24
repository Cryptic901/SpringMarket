package by.cryptic.categoryservice.controller.query;

import by.cryptic.utils.DTO.CategoryDTO;
import by.cryptic.categoryservice.service.query.CategoryGetAllQuery;
import by.cryptic.categoryservice.service.query.handler.CategoryGetAllQueryHandler;
import by.cryptic.categoryservice.service.query.handler.CategoryGetByIdQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get all categories", description = "return all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories found"),
            @ApiResponse(responseCode = "404", description = "Categories not found"),
    })
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryGetAllQueryHandler.handle(new CategoryGetAllQuery()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by id", description = "return category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
    })
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryGetByIdQueryHandler.handle(id));
    }
}