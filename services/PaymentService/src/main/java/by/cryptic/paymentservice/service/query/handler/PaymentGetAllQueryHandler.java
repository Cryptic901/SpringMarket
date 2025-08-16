package by.cryptic.paymentservice.service.query.handler;

import by.cryptic.paymentservice.mapper.PaymentMapper;
import by.cryptic.paymentservice.repository.read.PaymentViewRepository;
import by.cryptic.paymentservice.service.query.PaymentGetAllQuery;
import by.cryptic.utils.DTO.PaymentDTO;
import by.cryptic.utils.QueryHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentGetAllQueryHandler implements QueryHandler<PaymentGetAllQuery, List<PaymentDTO>> {

    private final PaymentViewRepository paymentRepository;

    @Override
    public List<PaymentDTO> handle(PaymentGetAllQuery command) {
        List<PaymentDTO> pays = paymentRepository.findAll().stream()
                .filter(payment -> payment.getUserId().equals(command.userId()))
                .map(PaymentMapper::toDto)
                .toList();

        if (pays.isEmpty()) {
            throw new EntityNotFoundException("There are no payments");
        }
        return pays;
    }
}
