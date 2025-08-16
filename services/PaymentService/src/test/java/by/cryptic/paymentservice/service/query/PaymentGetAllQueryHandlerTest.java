package by.cryptic.paymentservice.service.query;

import by.cryptic.paymentservice.mapper.PaymentMapper;
import by.cryptic.paymentservice.model.read.PaymentView;
import by.cryptic.paymentservice.repository.read.PaymentViewRepository;
import by.cryptic.paymentservice.service.query.handler.PaymentGetAllQueryHandler;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PaymentGetAllQueryHandlerTest {

    @Mock
    private PaymentViewRepository paymentViewRepository;

    @InjectMocks
    private PaymentGetAllQueryHandler paymentGetAllQueryHandler;

    @Test
    void getAllPayments_whenPaymentsExists_shouldReturnAllPayments() {
        //Arrange
        UUID orderId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        PaymentView paymentView = PaymentView.builder()
                .paymentId(paymentId)
                .paymentMethod(PaymentMethod.PAYPAL)
                .orderId(orderId)
                .userId(userId)
                .price(BigDecimal.valueOf(148.8))
                .build();
        PaymentDTO paymentDTO = PaymentMapper.toDto(paymentView);
        Mockito.when(paymentViewRepository.findAll()).thenReturn(Collections.singletonList(paymentView));
        //Act
        List<PaymentDTO> result = paymentGetAllQueryHandler.handle(new PaymentGetAllQuery(userId));
        //Assert
        assertEquals(Collections.singletonList(paymentDTO), result);
        Mockito.verify(paymentViewRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(paymentViewRepository);
    }

    @Test
    void getAllPayments_shouldReturnEntityNotFoundException() {
        //Arrange
        Mockito.when(paymentViewRepository.findAll()).thenReturn(Collections.emptyList());
        //Act
        //Assert
        assertThrows(EntityNotFoundException.class, () -> paymentGetAllQueryHandler.handle(new PaymentGetAllQuery(UUID.randomUUID())));
        Mockito.verify(paymentViewRepository, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(paymentViewRepository);
    }
}