package by.cryptic.springmarket.controller.command;

import by.cryptic.springmarket.dto.UserDTO;
import by.cryptic.springmarket.service.command.UserDeleteCommand;
import by.cryptic.springmarket.service.command.UserUpdateCommand;
import by.cryptic.springmarket.service.command.handler.user.UserDeleteCommandHandler;
import by.cryptic.springmarket.service.command.handler.user.UserUpdateCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserCommandController {

    private final UserUpdateCommandHandler userUpdateCommandHandler;
    private final UserDeleteCommandHandler userDeleteCommandHandler;

    @PatchMapping
    public ResponseEntity<UserDTO> updateUser(
            @RequestBody UserUpdateCommand userDTO) {
        userUpdateCommandHandler.handle(userDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        userDeleteCommandHandler.handle(new UserDeleteCommand());
        return ResponseEntity.noContent().build();
    }
}


