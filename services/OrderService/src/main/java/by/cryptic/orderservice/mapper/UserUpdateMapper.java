package by.cryptic.orderservice.mapper;

import by.cryptic.orderservice.model.write.UserReplica;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserUpdateMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReplica(@MappingTarget UserReplica replica, UserUpdatedEvent event);
}
