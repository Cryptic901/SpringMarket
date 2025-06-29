package by.cryptic.orderservice.controller.command;

import by.cryptic.orderservice.service.command.OrderCancelCommand;
import by.cryptic.orderservice.service.command.OrderCreateCommand;
import by.cryptic.orderservice.service.command.OrderCreateDTO;
import by.cryptic.orderservice.service.command.OrderUpdateCommand;
import by.cryptic.orderservice.service.command.handler.OrderCancelCommandHandler;
import by.cryptic.orderservice.service.command.handler.OrderCreateCommandHandler;
import by.cryptic.orderservice.service.command.handler.OrderUpdatedCommandHandler;
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
            @RequestBody OrderCreateDTO order,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Email") String userEmail) {
        orderCreateCommandHandler.handle(new OrderCreateCommand(
                order.location(),
                userId,
                userEmail
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateOrder(
            @RequestBody OrderUpdateCommand order) {
        orderUpdatedCommandHandler.handle(order);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cancel")
    public ResponseEntity<Void> cancelOrder(
            @RequestParam UUID id,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Email") String userEmail) {
        orderCancelCommandHandler.handle(new OrderCancelCommand(
                id,
                userId,
                userEmail));
        return ResponseEntity.noContent().build();
    }
}
