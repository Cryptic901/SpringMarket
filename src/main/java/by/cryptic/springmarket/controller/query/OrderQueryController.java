package by.cryptic.springmarket.controller.query;

import by.cryptic.springmarket.dto.FullOrderDTO;
import by.cryptic.springmarket.service.query.OrderDTO;
import by.cryptic.springmarket.service.query.OrderGetAllQuery;
import by.cryptic.springmarket.service.query.handler.order.OrderGetAllQueryHandler;
import by.cryptic.springmarket.service.query.handler.order.OrderGetByIdQueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderQueryController {

    private final OrderGetAllQueryHandler orderGetAllQueryHandler;
    private final OrderGetByIdQueryHandler orderGetByIdQueryHandler;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders() {
        return ResponseEntity.ok(orderGetAllQueryHandler.handle(new OrderGetAllQuery()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullOrderDTO> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderGetByIdQueryHandler.handle(id));
    }
}
