package by.cryptic.orderservice.mapper;

import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FullOrderMapper {
    OrderDTO toDto(CustomerOrderView customerOrder);
}
