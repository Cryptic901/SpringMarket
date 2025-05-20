package by.cryptic.springmarket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record VerifyUserDTO(@Email(message = "Invalid email")
                            String email,
                            Integer verificationCode) {
}
