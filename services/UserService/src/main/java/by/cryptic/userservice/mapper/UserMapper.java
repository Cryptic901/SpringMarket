package by.cryptic.userservice.mapper;

import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.model.read.AppUserView;
import by.cryptic.userservice.model.write.AppUser;
import by.cryptic.utils.event.user.UserUpdatedEvent;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDto(AppUserView user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .username(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public void updateEntity(AppUser user, UserUpdatedEvent dto) {
        if (user == null || dto == null) return;

        if (dto.getUserId() != null) {
            user.setId(dto.getUserId());
        }

        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }

        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
    }

    public void updateView(AppUserView user, UserUpdatedEvent event) {
        if (user == null || event == null) return;

        if (event.getUserId() != null) {
            user.setUserId(event.getUserId());
        }
        if (event.getUsername() != null) {
            user.setUsername(event.getUsername());
        }

        if (event.getPhoneNumber() != null) {
            user.setPhoneNumber(event.getPhoneNumber());
        }

        if (event.getFirstName() != null) {
            user.setFirstName(event.getFirstName());
        }

        if (event.getLastName() != null) {
            user.setLastName(event.getLastName());
        }
    }
}
