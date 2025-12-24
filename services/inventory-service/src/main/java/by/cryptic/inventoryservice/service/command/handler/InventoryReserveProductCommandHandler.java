package by.cryptic.inventoryservice.service.command.handler;

import by.cryptic.exceptions.OutOfStockException;
import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.model.Reservation;
import by.cryptic.inventoryservice.model.Warehouse;
import by.cryptic.inventoryservice.repository.InventoryRepository;
import by.cryptic.inventoryservice.repository.ReservationRepository;
import by.cryptic.inventoryservice.repository.WarehouseRepository;
import by.cryptic.inventoryservice.service.command.InventoryReserveProductCommand;
import by.cryptic.utils.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReserveProductCommandHandler implements CommandHandler<InventoryReserveProductCommand> {

    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public void handle(InventoryReserveProductCommand command) {
        Integer quantityToReserve = command.quantity();
        Warehouse warehouse = warehouseRepository.findClosestWarehousesWithEnoughProducts(command.lat(),
                command.lon(),
                1, quantityToReserve, command.productId()).getFirst();
        reserveInventory(command, quantityToReserve, warehouse);
        addReservation(command, quantityToReserve, warehouse);
    }

    private void reserveInventory(InventoryReserveProductCommand command, Integer quantityToReserve, Warehouse warehouse) {
        Inventory availableInventory = inventoryRepository
                .findAvailableByProductId(command.productId(), quantityToReserve, warehouse)
                .orElseThrow(() -> new OutOfStockException("There are not enough %s in our stock".formatted(command.productId())));
        availableInventory.reserve(quantityToReserve);
        inventoryRepository.save(availableInventory);
    }

    private void addReservation(InventoryReserveProductCommand command, Integer quantityToReserve, Warehouse warehouse) {
        Reservation reservation = reservationRepository
                .findByReservedByProductId(command.orderId(), command.productId())
                .orElse(Reservation.builder()
                        .productId(command.productId())
                        .quantityToReserve(0)
                        .warehouse(warehouse)
                        .orderId(command.orderId())
                        .build());

        reservation.addQuantity(quantityToReserve);
        reservationRepository.save(reservation);
    }
}
