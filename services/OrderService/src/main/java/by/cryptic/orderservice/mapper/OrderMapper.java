package by.cryptic.orderservice.mapper;

import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.dto.ShortOrderDTO;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.model.write.OrderProduct;
import by.cryptic.utils.DTO.OrderedProductDTO;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public static OrderDTO toDto(CustomerOrder order) {
        if (order == null) return null;

        return OrderDTO.builder()
                .location(order.getLocation())
                .orderStatus(order.getOrderStatus())
                .price(order.getPrice())
                .createdBy(order.getCreatedBy())
                .updatedBy(order.getUpdatedBy())
                .build();
    }

    public static OrderDTO toDto(CustomerOrderView order) {
        if (order == null) return null;

        return OrderDTO.builder()
                .location(order.getLocation())
                .orderStatus(order.getOrderStatus())
                .price(order.getPrice())
                .createdBy(order.getCreatedBy())
                .updatedBy(order.getUpdatedBy())
                .build();
    }

    public static OrderedProductDTO toOrderedDto(OrderProduct order) {
        if (order == null) return null;

        return new OrderedProductDTO(order.getProductId(), order.getQuantity());
    }

    public static void updateEntity(CustomerOrder order, ShortOrderDTO dto) {
        if (order == null || dto == null) return;

        if (dto.location() != null) {
            order.setLocation(dto.location());
        }
    }
}
