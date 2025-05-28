package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.OrderDTO;
import by.cryptic.springmarket.dto.ResponseOrderDTO;
import by.cryptic.springmarket.model.CustomerOrder;
import by.cryptic.springmarket.model.OrderProduct;
import by.cryptic.springmarket.model.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    CustomerOrder toEntity(OrderDTO orderDTO);

    OrderDTO toDto(CustomerOrder customerOrder);

    ResponseOrderDTO toResponseDto(CustomerOrder customerOrder);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget CustomerOrder order, OrderDTO orderDTO);

    OrderProduct toDto(Product product);
}
