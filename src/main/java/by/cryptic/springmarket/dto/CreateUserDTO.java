package by.cryptic.springmarket.dto;

import jakarta.validation.constraints.Email;

public record CreateUserDTO(String username,
                            String password,
                            @Email(message = "Email is invalid")
                            String email,
                            String phoneNumber,
                            Character gender) {
}
