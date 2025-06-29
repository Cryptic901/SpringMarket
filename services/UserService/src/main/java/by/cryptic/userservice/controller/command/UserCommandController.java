package by.cryptic.userservice.controller.command;

import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.dto.UserUpdateDTO;
import by.cryptic.userservice.service.command.UserDeleteCommand;
import by.cryptic.userservice.service.command.UserUpdateCommand;
import by.cryptic.userservice.service.command.handler.UserDeleteCommandHandler;
import by.cryptic.userservice.service.command.handler.UserUpdateCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserCommandController {

    private final UserUpdateCommandHandler userUpdateCommandHandler;
    private final UserDeleteCommandHandler userDeleteCommandHandler;

    @PatchMapping
    public ResponseEntity<UserDTO> updateUser(
            @RequestBody UserUpdateDTO userDTO,
            @RequestHeader("X-User-Id") UUID id) {
        userUpdateCommandHandler.handle(new UserUpdateCommand(id, userDTO.username(),
                userDTO.phoneNumber(), userDTO.role()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestHeader("X-User-Id") UUID id) {
        userDeleteCommandHandler.handle(new UserDeleteCommand(id));
        return ResponseEntity.noContent().build();
    }
}


