package by.cryptic.categoryservice.controller.command;

import by.cryptic.categoryservice.dto.CategoryUpdateDTO;
import by.cryptic.categoryservice.service.command.CategoryCreateCommand;
import by.cryptic.categoryservice.service.command.CategoryDeleteCommand;
import by.cryptic.categoryservice.service.command.CategoryUpdateCommand;
import by.cryptic.categoryservice.service.command.handler.CategoryCreateCommandHandler;
import by.cryptic.categoryservice.service.command.handler.CategoryDeleteCommandHandler;
import by.cryptic.categoryservice.service.command.handler.CategoryUpdateCommandHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Create category", description = "creating category." +
            " Can only be done with the ROLE_ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Request parameters are incorrect"),
            @ApiResponse(responseCode = "403", description = "Request denied (ROLE_ADMIN REQUIRED)"),
            @ApiResponse(responseCode = "503", description = "The creation failed because the server is down")
    })
    public ResponseEntity<Void> createCategory(
            @RequestBody CategoryCreateCommand categoryDTO) {
        categoryCreateCommandHandler.handle(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{categoryId}")
    @Operation(summary = "Update category", description = "updating category." +
            " Can only be done with the ROLE_ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category updated"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Request denied (ROLE_ADMIN REQUIRED)"),
            @ApiResponse(responseCode = "503", description = "The updating failed because the server is down")
    })
    public ResponseEntity<Void> updateCategory(@PathVariable UUID categoryId,
                                               @RequestBody CategoryUpdateDTO categoryDTO) {
        categoryUpdateCommandHandler.handle(new CategoryUpdateCommand(categoryId,
                categoryDTO.name(),
                categoryDTO.description()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "deleting category." +
            "Can only be done with the ROLE_ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "403", description = "Request denied (ROLE_ADMIN REQUIRED)"),
            @ApiResponse(responseCode = "503", description = "The creation failed because the server is down")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryDeleteCommandHandler.handle(new CategoryDeleteCommand(id));
        return ResponseEntity.noContent().build();
    }
}