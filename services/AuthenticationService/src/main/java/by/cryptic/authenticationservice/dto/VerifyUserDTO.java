package by.cryptic.authenticationservice.dto;

import jakarta.validation.constraints.Email;

public record VerifyUserDTO(@Email(message = "Invalid email")
                            String email,
                            Integer verificationCode) {
}
