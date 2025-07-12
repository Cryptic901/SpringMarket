package by.cryptic.userservice.dto;

import by.cryptic.utils.Role;

public record UserUpdateDTO(String username,
                            String phoneNumber,
                            Role role) {
}
