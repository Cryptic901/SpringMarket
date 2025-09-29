package by.cryptic.orderservice.controller.query;

import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.service.query.OrderGetAllQuery;
import by.cryptic.orderservice.service.query.OrderGetByIdQuery;
import by.cryptic.orderservice.service.query.handler.OrderGetAllQueryHandler;
import by.cryptic.orderservice.service.query.handler.OrderGetByIdQueryHandler;
import by.cryptic.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderQueryController {

    private final OrderGetAllQueryHandler orderGetAllQueryHandler;
    private final OrderGetByIdQueryHandler orderGetByIdQueryHandler;

    @GetMapping
    @Operation(summary = "Get all orders", description = "return all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found"),
            @ApiResponse(responseCode = "404", description = "Orders not found"),
    })
    public ResponseEntity<List<OrderDTO>> getOrders(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(orderGetAllQueryHandler
                .handle(new OrderGetAllQuery(JwtUtil.extractUserId(jwt))));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by id", description = "return order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
    })
    public ResponseEntity<OrderDTO> getOrder(@PathVariable UUID orderId, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(orderGetByIdQueryHandler.handle
                (new OrderGetByIdQuery(orderId, JwtUtil.extractUserId(jwt))));
    }
}
