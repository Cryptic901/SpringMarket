package by.cryptic.inventoryservice.model;

import by.cryptic.utils.enums.WarehouseType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.CreatedDate;
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
@Table(name = "warehouse", schema = "inventory_schema")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "location", columnDefinition = "GEOGRAPHY(Point, 4326)")
    private Point location;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouse", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonManagedReference
    private List<Inventory> products = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "warehouse", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonManagedReference
    private List<Reservation> reservations = new ArrayList<>();

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private WarehouseType type;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "capacity", nullable = false)
    private Long capacity;

    @Column(name = "current_load")
    private Long currentLoad = 0L;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Warehouse warehouse = (Warehouse) o;
        return Objects.equals(id, warehouse.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
