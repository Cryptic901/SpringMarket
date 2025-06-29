package by.cryptic.userservice.dto;

import by.cryptic.utils.Role;
import jakarta.validation.constraints.Email;

public record UserUpdateDTO(String username,
                            String phoneNumber,
                            Role role) {
}
