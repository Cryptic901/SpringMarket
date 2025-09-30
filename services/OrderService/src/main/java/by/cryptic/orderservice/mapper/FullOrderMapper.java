package by.cryptic.orderservice.mapper;

import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.model.read.CustomerOrderView;
import org.springframework.stereotype.Component;

@Component
public class FullOrderMapper {

   public static OrderDTO toDto(CustomerOrderView customerOrder) {
       if (customerOrder == null) {
           return null;
       }
       return OrderDTO.builder()
               .price(customerOrder.getPrice())
               .orderStatus(customerOrder.getOrderStatus())
               .createdBy(customerOrder.getCreatedBy())
               .location(customerOrder.getLocation())
               .updatedBy(customerOrder.getUpdatedBy())
               .build();
    }
}
