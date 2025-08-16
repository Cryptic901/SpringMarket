package by.cryptic.paymentservice.mapper;

import by.cryptic.paymentservice.model.read.PaymentView;
import by.cryptic.utils.DTO.PaymentDTO;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public static PaymentDTO toDto(PaymentView payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentDTO(payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getOrderId(),
                payment.getPrice());
    }
}
