package by.cryptic.userservice.mapper;

import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.model.read.AppUserView;
import by.cryptic.userservice.model.write.AppUser;
import by.cryptic.userservice.service.command.UserUpdateCommand;
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

    public void updateEntity(AppUser user, UserUpdateCommand dto) {
        if (user == null || dto == null) return;

        if (dto.userId() != null) {
            user.setId(dto.userId());
        }
        if (dto.username() != null) {
            user.setUsername(dto.username());
        }

        if (dto.phoneNumber() != null) {
            user.setPhoneNumber(dto.phoneNumber());
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
    }
}
