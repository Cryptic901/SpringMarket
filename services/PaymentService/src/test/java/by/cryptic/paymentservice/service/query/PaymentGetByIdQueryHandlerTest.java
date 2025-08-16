package by.cryptic.paymentservice.service.query;

import by.cryptic.paymentservice.mapper.PaymentMapper;
import by.cryptic.paymentservice.model.read.PaymentView;
import by.cryptic.paymentservice.repository.read.PaymentViewRepository;
import by.cryptic.paymentservice.service.query.handler.PaymentGetByIdQueryHandler;
import by.cryptic.utils.DTO.PaymentDTO;
import by.cryptic.utils.PaymentMethod;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PaymentGetByIdQueryHandlerTest {

    @Mock
    private PaymentViewRepository paymentRepository;

    @InjectMocks
    private PaymentGetByIdQueryHandler paymentGetByIdQueryHandler;

    @Test
    void getPaymentById_withValidUUID_shouldReturnPayment() {
        //Arrange
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        PaymentView paymentView = PaymentView.builder()
                .paymentId(paymentId)
                .paymentMethod(PaymentMethod.PAYPAL)
                .orderId(orderId)
                .userId(userId)
                .price(BigDecimal.valueOf(148.8))
                .build();
        PaymentDTO paymentDTO = PaymentMapper.toDto(paymentView);
        Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(paymentView));
        //Act
        PaymentDTO result = paymentGetByIdQueryHandler.handle(new PaymentGetByIdQuery(paymentId));
        //Assert
        assertEquals(paymentDTO, result);
        Mockito.verify(paymentRepository, Mockito.times(1)).findById(paymentId);
        Mockito.verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void getPaymentById_withInvalidUUID_shouldThrowEntityNotFoundException() {
        //Arrange
        UUID paymentId = UUID.randomUUID();
        Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> paymentGetByIdQueryHandler.handle(new PaymentGetByIdQuery(paymentId)));
        Mockito.verify(paymentRepository, Mockito.times(1)).findById(paymentId);
        Mockito.verifyNoMoreInteractions(paymentRepository);
    }
}