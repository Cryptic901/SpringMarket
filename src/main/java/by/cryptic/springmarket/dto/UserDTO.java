package by.cryptic.springmarket.dto;

import by.cryptic.springmarket.enums.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String username;
    @Email(message = "Email is invalid")
    private String email;
    private String phoneNumber;
    private Role role;

}
