package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.ShortOrderDTO;
import by.cryptic.springmarket.event.order.OrderUpdatedEvent;
import by.cryptic.springmarket.model.read.CustomerOrderView;
import by.cryptic.springmarket.model.write.CustomerOrder;
import by.cryptic.springmarket.model.write.OrderProduct;
import by.cryptic.springmarket.model.write.Product;
import by.cryptic.springmarket.service.query.OrderDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    CustomerOrder toEntity(ShortOrderDTO orderDTO);

    ShortOrderDTO toShortDto(CustomerOrder customerOrder);

    OrderDTO toDto(CustomerOrder customerOrder);
    OrderDTO toDto(CustomerOrderView customerOrderView);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget CustomerOrder order, ShortOrderDTO orderDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateView(@MappingTarget CustomerOrderView order, OrderUpdatedEvent event);

    OrderProduct toProductDto(Product product);
}
