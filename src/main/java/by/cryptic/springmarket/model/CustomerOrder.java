package by.cryptic.springmarket.model;

import by.cryptic.springmarket.enums.OrderStatus;
import by.cryptic.springmarket.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "orders", schema = "public")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<OrderProduct> products = new ArrayList<>();

    @Column(nullable = false)
    private String location;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private AppUser appUser;

    @CreatedDate
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(nullable = false, updatable = false, name = "created_by")
    private UUID createdBy;

    @LastModifiedBy
    @Column(insertable = false, name = "updated_by")
    private UUID updatedBy;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CustomerOrder order = (CustomerOrder) o;
        return Objects.equals(id, order.id) &&
                Objects.equals(location, order.location) &&
                Objects.equals(createdAt, order.createdAt) &&
                Objects.equals(updatedAt, order.updatedAt) &&
                Objects.equals(createdBy, order.createdBy) &&
                Objects.equals(updatedBy, order.updatedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location, createdAt, updatedAt, createdBy, updatedBy);
    }
}
