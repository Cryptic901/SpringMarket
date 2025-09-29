package by.cryptic.paymentservice.controller;

import by.cryptic.paymentservice.service.query.PaymentGetAllQuery;
import by.cryptic.paymentservice.service.query.PaymentGetByIdQuery;
import by.cryptic.paymentservice.service.query.handler.PaymentGetAllQueryHandler;
import by.cryptic.paymentservice.service.query.handler.PaymentGetByIdQueryHandler;
import by.cryptic.security.JwtUtil;
import by.cryptic.utils.DTO.PaymentDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentQueryController {

    private final PaymentGetAllQueryHandler paymentGetAllQueryHandler;
    private final PaymentGetByIdQueryHandler paymentGetByIdQueryHandler;

    @GetMapping
    @Operation(summary = "Get all payments", description = "return all payments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payments found"),
            @ApiResponse(responseCode = "404", description = "Payments not found"),
    })
    public ResponseEntity<List<PaymentDTO>> getAllPayments(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(paymentGetAllQueryHandler.handle(new PaymentGetAllQuery(JwtUtil.extractUserId(jwt))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by id", description = "return payment by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "404", description = "Payment not found"),
    })
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentGetByIdQueryHandler.handle(new PaymentGetByIdQuery(id)));
    }
}
