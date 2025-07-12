package by.cryptic.paymentservice.controller.write;

import by.cryptic.paymentservice.service.command.PaymentCreateCommand;
import by.cryptic.paymentservice.service.command.handler.PaymentCreateCommandHandler;
import by.cryptic.utils.DTO.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentCommandController {

    private final PaymentCreateCommandHandler paymentCreateCommandHandler;

    @PostMapping
    public ResponseEntity<Void> createPayment(@RequestBody PaymentDTO dto,
                                              @RequestHeader("X-User-Id") UUID userId,
                                              @RequestHeader("X-User-Email") String email) {
        paymentCreateCommandHandler.handle(new PaymentCreateCommand(
                dto.paymentMethod(),
                userId,
                dto.orderId(),
                email,
                dto.price(),
                LocalDateTime.now()
        ));
        return ResponseEntity.ok().build();
    }
}
