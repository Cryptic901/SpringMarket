package by.cryptic.orderservice.controller.command;

import by.cryptic.orderservice.dto.OrderUpdateDTO;
import by.cryptic.orderservice.service.command.OrderCancelCommand;
import by.cryptic.orderservice.service.command.OrderCreateCommand;
import by.cryptic.orderservice.service.command.OrderCreateDTO;
import by.cryptic.orderservice.service.command.OrderUpdateCommand;
import by.cryptic.orderservice.service.command.handler.OrderCancelCommandHandler;
import by.cryptic.orderservice.service.command.handler.OrderCreateCommandHandler;
import by.cryptic.orderservice.service.command.handler.OrderUpdatedCommandHandler;
import by.cryptic.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
            @RequestBody OrderCreateDTO order, @AuthenticationPrincipal Jwt jwt) {
        orderCreateCommandHandler.handle(new OrderCreateCommand(
                order.location(),
                JwtUtil.extractUserId(jwt),
                JwtUtil.extractEmail(jwt)
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping
    public ResponseEntity<Void> updateOrder(
            @RequestBody OrderUpdateDTO order, @AuthenticationPrincipal Jwt jwt) {
        orderUpdatedCommandHandler.handle(
                new OrderUpdateCommand(
                        order.orderId(),
                        order.location(),
                        JwtUtil.extractUserId(jwt),
                        JwtUtil.extractEmail(jwt)
                )
        );
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cancel")
    public ResponseEntity<Void> cancelOrder(
            @RequestParam UUID id, @AuthenticationPrincipal Jwt jwt) {
        orderCancelCommandHandler.handle(new OrderCancelCommand(
                id,
                JwtUtil.extractUserId(jwt),
                JwtUtil.extractEmail(jwt))
        );
        return ResponseEntity.noContent().build();
    }
}
