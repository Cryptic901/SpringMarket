package by.cryptic.userservice.controller.command;

import by.cryptic.security.JwtUtil;
import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.dto.UserUpdateDTO;
import by.cryptic.userservice.service.command.UserDeleteCommand;
import by.cryptic.userservice.service.command.UserUpdateCommand;
import by.cryptic.userservice.service.command.handler.UserDeleteCommandHandler;
import by.cryptic.userservice.service.command.handler.UserUpdateCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserCommandController {

    private final UserUpdateCommandHandler userUpdateCommandHandler;
    private final UserDeleteCommandHandler userDeleteCommandHandler;

    @PatchMapping
    public ResponseEntity<UserDTO> updateUser(
            @RequestBody UserUpdateDTO userDTO, @AuthenticationPrincipal Jwt jwt) {
        userUpdateCommandHandler.handle(new UserUpdateCommand(JwtUtil.extractUserId(jwt), userDTO.username(),
                userDTO.phoneNumber()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        userDeleteCommandHandler.handle(new UserDeleteCommand(JwtUtil.extractUserId(jwt)));
        return ResponseEntity.noContent().build();
    }
}


