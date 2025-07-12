package by.cryptic.paymentservice.controller.read;

import by.cryptic.paymentservice.service.query.PaymentGetAllQuery;
import by.cryptic.paymentservice.service.query.PaymentGetByIdQuery;
import by.cryptic.paymentservice.service.query.handler.PaymentGetAllQueryHandler;
import by.cryptic.paymentservice.service.query.handler.PaymentGetByIdQueryHandler;
import by.cryptic.utils.DTO.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentQueryController {

    private final PaymentGetAllQueryHandler paymentGetAllQueryHandler;
    private final PaymentGetByIdQueryHandler paymentGetByIdQueryHandler;

    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return ResponseEntity.ok(paymentGetAllQueryHandler.handle(new PaymentGetAllQuery(userId)));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(
            @PathVariable UUID paymentId
    ) {
        return ResponseEntity.ok(paymentGetByIdQueryHandler.handle(new PaymentGetByIdQuery(paymentId)));
    }
}
