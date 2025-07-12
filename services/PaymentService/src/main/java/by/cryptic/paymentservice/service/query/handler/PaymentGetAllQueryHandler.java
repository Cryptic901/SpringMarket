package by.cryptic.paymentservice.service.query.handler;

import by.cryptic.paymentservice.mapper.PaymentMapper;
import by.cryptic.paymentservice.repository.write.PaymentRepository;
import by.cryptic.paymentservice.service.query.PaymentGetAllQuery;
import by.cryptic.utils.DTO.PaymentDTO;
import by.cryptic.utils.QueryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentGetAllQueryHandler implements QueryHandler<PaymentGetAllQuery, List<PaymentDTO>> {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public List<PaymentDTO> handle(PaymentGetAllQuery command) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getUserId().equals(command.userId()))
                .map(paymentMapper::toDto)
                .toList();
    }
}
