package by.cryptic.orderservice.mapper;

import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.dto.ShortOrderDTO;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import by.cryptic.orderservice.model.write.CustomerOrder;
import by.cryptic.utils.event.order.OrderUpdatedEvent;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderDTO toDto(CustomerOrderView view) {
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

    public void updateEntity(CustomerOrder order, ShortOrderDTO dto) {
        if (order == null || dto == null) return;

        if (dto.location() != null) {
            order.setLocation(dto.location());
        }
    }

    public void updateView(CustomerOrderView view, OrderUpdatedEvent event) {
        if (view == null || event == null) return;

        if (event.getStatus() != null) {
            view.setOrderStatus(event.getStatus());
        }
        if (event.getUpdatedTimestamp() != null) {
            view.setUpdatedAt(event.getUpdatedTimestamp());
        }

        if (event.getOrderId() != null) {
            view.setOrderId(event.getOrderId());
        }

    }
}
