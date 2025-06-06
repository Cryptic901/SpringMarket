package by.cryptic.springmarket.controller.command;

import by.cryptic.springmarket.service.command.OrderCancelCommand;
import by.cryptic.springmarket.service.command.OrderCreateCommand;
import by.cryptic.springmarket.service.command.OrderUpdateCommand;
import by.cryptic.springmarket.service.command.handler.order.OrderCancelCommandHandler;
import by.cryptic.springmarket.service.command.handler.order.OrderCreateCommandHandler;
import by.cryptic.springmarket.service.command.handler.order.OrderUpdatedCommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderCommandController {

    private final OrderCreateCommandHandler orderCreateCommandHandler;
    private final OrderUpdatedCommandHandler orderUpdatedCommandHandler;
    private final OrderCancelCommandHandler orderCancelCommandHandler;

    @PostMapping
    public ResponseEntity<Void> createOrder(
            @RequestBody OrderCreateCommand order) {
        orderCreateCommandHandler.handle(order);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateOrder(
            @RequestBody OrderUpdateCommand order) {
        orderUpdatedCommandHandler.handle(order);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID id) {
        orderCancelCommandHandler.handle(new OrderCancelCommand(id));
        return ResponseEntity.noContent().build();
    }
}
