package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.OrderDTO;
import by.cryptic.springmarket.model.read.CustomerOrderView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FullOrderMapper {
    OrderDTO toDto(CustomerOrderView customerOrder);
}
