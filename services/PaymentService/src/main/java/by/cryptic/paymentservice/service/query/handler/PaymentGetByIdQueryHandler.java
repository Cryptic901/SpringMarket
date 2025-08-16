package by.cryptic.paymentservice.service.query.handler;

import by.cryptic.paymentservice.mapper.PaymentMapper;
import by.cryptic.paymentservice.repository.read.PaymentViewRepository;
import by.cryptic.paymentservice.service.query.PaymentGetByIdQuery;
import by.cryptic.utils.DTO.PaymentDTO;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentGetByIdQueryHandler implements QueryHandler<PaymentGetByIdQuery, PaymentDTO> {

    private final PaymentViewRepository paymentRepository;

    @Override
    @Cacheable(cacheNames = "payments", key = "'payment:' + #command.paymentId()")
    public PaymentDTO handle(PaymentGetByIdQuery command) {
        return PaymentMapper.toDto(paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new EntityNotFoundException("Payment with id %s not found"
                        .formatted(command.paymentId()))));
    }
}
