package by.cryptic.springmarket.controller;

import by.cryptic.springmarket.dto.FullOrderDTO;
import by.cryptic.springmarket.dto.OrderDTO;
import by.cryptic.springmarket.dto.OrderResponse;
import by.cryptic.springmarket.dto.ResponseOrderDTO;
import by.cryptic.springmarket.model.AppUser;
import by.cryptic.springmarket.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<ResponseOrderDTO>> getOrders(@AuthenticationPrincipal AppUser user) {
        return ResponseEntity.ok(orderService.getAllOrders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FullOrderDTO> getOrder(@PathVariable UUID id, @AuthenticationPrincipal AppUser user) {
        return ResponseEntity.ok(orderService.getOrderById(id, user));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderDTO order,
            @AuthenticationPrincipal(errorOnInvalidType = true) AppUser appUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(order, appUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FullOrderDTO> updateOrder(
            @PathVariable UUID id,
            @RequestBody OrderDTO order,
            @AuthenticationPrincipal AppUser appUser) {
        return ResponseEntity.ok(orderService.updateOrder(id, order, appUser));
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID id,
            @AuthenticationPrincipal AppUser appUser) {
        return ResponseEntity.ok(orderService.cancelOrder(id, appUser));
    }
}
