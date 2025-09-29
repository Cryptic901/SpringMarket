package by.cryptic.orderservice.controller.command;

import by.cryptic.orderservice.service.command.OrderCancelCommand;
import by.cryptic.orderservice.service.command.OrderCreateCommand;
import by.cryptic.orderservice.service.command.OrderCreateDTO;
import by.cryptic.orderservice.service.command.handler.OrderCancelCommandHandler;
import by.cryptic.orderservice.service.command.handler.OrderCreateCommandHandler;
import by.cryptic.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
    private final OrderCancelCommandHandler orderCancelCommandHandler;

    @PostMapping
    @Operation(summary = "Create order", description = "creating order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Request parameters are incorrect"),
            @ApiResponse(responseCode = "404", description = "Cart products are not found"),
            @ApiResponse(responseCode = "409", description = "Not enough products to create order"),
            @ApiResponse(responseCode = "503", description = "The creation failed because the server is down")
    })
    public ResponseEntity<Void> createOrder(
            @RequestBody @Valid OrderCreateDTO order, @AuthenticationPrincipal Jwt jwt) {
        orderCreateCommandHandler.handle(new OrderCreateCommand(
                order.location(),
                order.paymentMethod(),
                JwtUtil.extractUserId(jwt),
                JwtUtil.extractEmail(jwt)
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/cancel/{id}")
    @Operation(summary = "Cancel order", description = "cancelling order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order not completed"),
            @ApiResponse(responseCode = "503", description = "The cancelling failed because the server is down")
    })
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        orderCancelCommandHandler.handle(new OrderCancelCommand(
                id,
                JwtUtil.extractUserId(jwt),
                JwtUtil.extractEmail(jwt))
        );
        return ResponseEntity.noContent().build();
    }
}
