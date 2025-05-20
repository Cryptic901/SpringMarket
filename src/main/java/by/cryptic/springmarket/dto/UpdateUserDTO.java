package by.cryptic.springmarket.dto;

public record UpdateUserDTO(String username,
                            String password,
                            String email,
                            String phoneNumber,
                            Character gender) {
}
