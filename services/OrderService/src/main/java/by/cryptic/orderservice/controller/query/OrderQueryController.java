package by.cryptic.orderservice.controller.query;

import by.cryptic.orderservice.dto.OrderDTO;
import by.cryptic.orderservice.service.query.OrderGetAllQuery;
import by.cryptic.orderservice.service.query.OrderGetByIdQuery;
import by.cryptic.orderservice.service.query.handler.OrderGetAllQueryHandler;
import by.cryptic.orderservice.service.query.handler.OrderGetByIdQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<OrderDTO>> getOrders(@RequestHeader("X-User-Id") UUID id) {
        return ResponseEntity.ok(orderGetAllQueryHandler.handle(new OrderGetAllQuery(id)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable UUID orderId,
                                             @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(orderGetByIdQueryHandler.handle
                (new OrderGetByIdQuery(orderId, userId)));
    }
}
