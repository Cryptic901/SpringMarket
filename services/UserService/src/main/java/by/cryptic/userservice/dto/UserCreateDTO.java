package by.cryptic.userservice.dto;

import jakarta.validation.constraints.Email;

public record UserCreateDTO(String username, @Email String email) {
}
