package by.cryptic.paymentservice.service.query.handler;

import by.cryptic.paymentservice.mapper.PaymentMapper;
import by.cryptic.paymentservice.repository.write.PaymentRepository;
import by.cryptic.paymentservice.service.query.PaymentGetByIdQuery;
import by.cryptic.utils.DTO.PaymentDTO;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentGetByIdQueryHandler implements QueryHandler<PaymentGetByIdQuery, PaymentDTO> {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDTO handle(PaymentGetByIdQuery command) {
        return paymentMapper.toDto(paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new EntityNotFoundException("Payment with id %s not found"
                        .formatted(command.paymentId()))));
    }
}
