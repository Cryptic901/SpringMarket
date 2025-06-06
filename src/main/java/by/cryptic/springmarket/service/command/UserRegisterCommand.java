package by.cryptic.springmarket.service.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterCommand(@Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
                                  String username,
                                  @NotBlank(message = "Password should be not null")
                                  String password,
                                  @Email(message = "Email is invalid")
                                  String email,
                                  @NotBlank(message = "Phone number should be not null")
                                  String phoneNumber,
                                  Character gender) {
}
