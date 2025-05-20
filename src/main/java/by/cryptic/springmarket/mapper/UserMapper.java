package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.RegisterUserDTO;
import by.cryptic.springmarket.dto.UpdateUserDTO;
import by.cryptic.springmarket.dto.UserDTO;
import by.cryptic.springmarket.model.AppUser;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(AppUser user);
    AppUser toEntity(UserDTO user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget AppUser user, UpdateUserDTO dto);
}
