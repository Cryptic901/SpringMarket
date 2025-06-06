package by.cryptic.springmarket.service.command;

import by.cryptic.springmarket.enums.Role;
import jakarta.validation.constraints.Email;

public record UserUpdateCommand(String username,
                                @Email(message = "Email is invalid")
                                String email,
                                String phoneNumber,
                                Role role) {
}
