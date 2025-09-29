package by.cryptic.userservice.controller;

import by.cryptic.security.JwtUtil;
import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.service.UserGetAllQuery;
import by.cryptic.userservice.service.handler.UserGetAllQueryHandler;
import by.cryptic.userservice.service.handler.UserGetByIdQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserQueryController {

    private final UserGetAllQueryHandler userGetAllQueryHandler;
    private final UserGetByIdQueryHandler userGetByIdQueryHandler;

    @GetMapping
    @Operation(summary = "Get all users", description = "return list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "404", description = "Users not found")
    })
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userGetAllQueryHandler.handle(new UserGetAllQuery()));
    }

    @GetMapping("/me")
    @Operation(summary = "Get yourself", description = "return your user data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "401", description = "Failed, because you are not authorized")
    })
    public ResponseEntity<UserDTO> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userGetByIdQueryHandler.handle(JwtUtil.extractUserId(jwt)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id", description = "return user founded by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userGetByIdQueryHandler.handle(id));
    }
}
