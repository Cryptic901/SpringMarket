package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.UserDTO;
import by.cryptic.springmarket.event.user.UserUpdatedEvent;
import by.cryptic.springmarket.model.read.AppUserView;
import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.service.command.UserUpdateCommand;
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
