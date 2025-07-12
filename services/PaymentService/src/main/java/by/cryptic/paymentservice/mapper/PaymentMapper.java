package by.cryptic.paymentservice.mapper;

import by.cryptic.paymentservice.model.write.Payment;
import by.cryptic.utils.DTO.PaymentDTO;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDTO toDto(Payment payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentDTO(payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getOrderId(),
                payment.getPrice());
    }
}
