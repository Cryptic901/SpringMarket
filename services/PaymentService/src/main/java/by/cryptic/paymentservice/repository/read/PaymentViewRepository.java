package by.cryptic.paymentservice.repository.read;

import by.cryptic.paymentservice.model.read.PaymentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentViewRepository extends JpaRepository<PaymentView, UUID> {
}
