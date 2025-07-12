package by.cryptic.userservice.service.command;


import java.util.UUID;

public record UserUpdateCommand(UUID userId,
                                String username,
                                String phoneNumber) {
}
