package by.cryptic.utils.DTO;

import by.cryptic.utils.enums.WarehouseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {
    private String name;
    private double longitude;
    private double latitude;
    private WarehouseType warehouseType;
    private Long capacity;
    private LocalDateTime createdAt;
}
