package by.cryptic.springmarket.mapper;

import by.cryptic.springmarket.dto.OrderDTO;
import by.cryptic.springmarket.dto.ShortOrderDTO;
import by.cryptic.springmarket.event.order.OrderUpdatedEvent;
import by.cryptic.springmarket.model.read.CustomerOrderView;
import by.cryptic.springmarket.model.write.CustomerOrder;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDto(CustomerOrderView customerOrderView);
    OrderDTO toDto(CustomerOrder customerOrder);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget CustomerOrder order, ShortOrderDTO orderDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateView(@MappingTarget CustomerOrderView order, OrderUpdatedEvent event);
}
