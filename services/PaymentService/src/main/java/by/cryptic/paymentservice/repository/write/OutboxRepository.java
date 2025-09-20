package by.cryptic.paymentservice.repository.write;

import by.cryptic.paymentservice.model.write.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {
    void deleteByCreatedAtBefore(LocalDateTime createdAtBefore);
}
