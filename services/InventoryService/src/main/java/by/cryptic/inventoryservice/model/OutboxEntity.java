package by.cryptic.inventoryservice.model;

import by.cryptic.utils.event.DomainEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "outbox", schema = "inventory_schema")
public class OutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "aggregate_type")
    private String aggregateType;

    @Column(name = "aggregate_id")
    private UUID aggregateId;

    @Column(name = "event_type")
    private String eventType;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private DomainEvent payload;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        OutboxEntity outbox = (OutboxEntity) object;
        return Objects.equals(id, outbox.id) && Objects.equals(aggregateType, outbox.aggregateType) && Objects.equals(aggregateId, outbox.aggregateId) && Objects.equals(eventType, outbox.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, aggregateType, aggregateId, eventType);
    }
}
