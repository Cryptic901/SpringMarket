package by.cryptic.orderservice.mapper;

import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.dto.ShortOrderDTO;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.orderservice.model.write.OrderProduct;
import by.cryptic.utils.DTO.OrderedProductDTO;
import by.cryptic.utils.event.order.OrderCanceledEvent;
import by.cryptic.utils.event.order.OrderUpdatedEvent;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDTO viewToDto(CustomerOrderView view) {
        if (view == null) return null;

        return OrderDTO.builder()
                .location(view.getLocation())
                .orderStatus(view.getOrderStatus())
                .price(view.getPrice())
                .createdBy(view.getCreatedBy())
                .updatedBy(view.getUpdatedBy())
                .build();
    }

    public OrderDTO toDto(CustomerOrder order) {
        if (order == null) return null;

        return OrderDTO.builder()
                .location(order.getLocation())
                .orderStatus(order.getOrderStatus())
                .price(order.getPrice())
                .createdBy(order.getCreatedBy())
                .updatedBy(order.getUpdatedBy())
                .build();
    }

    public OrderedProductDTO toOrderedDto(OrderProduct order) {
        if (order == null) return null;

        return new OrderedProductDTO(order.getProductId(), order.getQuantity());
    }

    public void updateEntity(CustomerOrder order, ShortOrderDTO dto) {
        if (order == null || dto == null) return;

        if (dto.location() != null) {
            order.setLocation(dto.location());
        }
    }

    public void updateView(CustomerOrderView view, OrderCanceledEvent event) {
        if (view == null || event == null) return;

        if (event.getOrderStatus() != null) {
            view.setOrderStatus(event.getOrderStatus());
        }

        if (event.getOrderId() != null) {
            view.setOrderId(event.getOrderId());
        }

    }
}
