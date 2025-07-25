package by.cryptic.userservice.controller.query;

import by.cryptic.security.JwtUtil;
import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.service.query.UserGetAllQuery;
import by.cryptic.userservice.service.query.handler.UserGetAllQueryHandler;
import by.cryptic.userservice.service.query.handler.UserGetByIdQueryHandler;
import by.cryptic.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userGetAllQueryHandler.handle(new UserGetAllQuery()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        if (!id.equals(JwtUtil.extractUserId(jwt)) &&
                !JwtUtil.hasRole(Role.ROLE_ADMIN, jwt)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(userGetByIdQueryHandler.handle(id));
    }
}
