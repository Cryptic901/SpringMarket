package by.cryptic.userservice.mapper;

import by.cryptic.userservice.dto.UserDTO;
import by.cryptic.userservice.model.read.AppUserView;
import by.cryptic.userservice.model.write.AppUser;
import by.cryptic.userservice.service.command.UserUpdateCommand;
import by.cryptic.utils.event.user.UserUpdatedEvent;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(AppUser user);
    UserDTO toDto(AppUserView user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget AppUser user, UserUpdateCommand dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateView(@MappingTarget AppUserView user, UserUpdatedEvent event);
}
