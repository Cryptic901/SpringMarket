package by.cryptic.userservice.service.command;

import by.cryptic.utils.Role;

import java.util.UUID;

public record UserUpdateCommand(UUID userId,
                                String username,
                                String phoneNumber,
                                Role role) {
}
