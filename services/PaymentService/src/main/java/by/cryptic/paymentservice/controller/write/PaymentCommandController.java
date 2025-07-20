package by.cryptic.paymentservice.controller.write;

import by.cryptic.paymentservice.service.command.PaymentCreateCommand;
import by.cryptic.paymentservice.service.command.handler.PaymentCreateCommandHandler;
import by.cryptic.security.JwtUtil;
import by.cryptic.utils.DTO.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
                                              @AuthenticationPrincipal Jwt jwt) {
        paymentCreateCommandHandler.handle(new PaymentCreateCommand(
                dto.paymentMethod(),
                JwtUtil.extractUserId(jwt),
                dto.orderId(),
                JwtUtil.extractEmail(jwt),
                dto.price(),
                LocalDateTime.now()
        ));
        return ResponseEntity.ok().build();
    }
}
