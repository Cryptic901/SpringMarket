package by.cryptic.springmarket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserDTO {
    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters")
    private String username;
    @NotBlank(message = "Password should be not null")
    private String password;
    @Email(message = "Email is invalid")
    private String email;
    @NotBlank(message = "Phone number should be not null")
    private String phoneNumber;
    private Character gender;
}
