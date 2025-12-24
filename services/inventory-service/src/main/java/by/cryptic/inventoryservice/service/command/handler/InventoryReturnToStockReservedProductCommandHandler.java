package by.cryptic.inventoryservice.service.command.handler;

import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.model.Reservation;
import by.cryptic.inventoryservice.repository.InventoryRepository;
import by.cryptic.inventoryservice.repository.ReservationRepository;
import by.cryptic.inventoryservice.service.command.InventoryReturnToStockReservedProductCommand;
import by.cryptic.utils.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReturnToStockReservedProductCommandHandler implements CommandHandler<InventoryReturnToStockReservedProductCommand> {

    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public void handle(InventoryReturnToStockReservedProductCommand command) {
        List<Reservation> reservations = reservationRepository
                .findAllByOrderId(command.orderId());

        if (reservations.isEmpty()) {
            log.warn("No reservations found for {}", command.orderId());
            return;
        }

        List<Inventory> inventories = new ArrayList<>();

        for (Reservation reservation : reservations) {
            Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Not found inventory for product " + reservation.getProductId()));

            inventory.returnToStock(reservation.getQuantityToReserve());
            inventories.add(inventory);
        }
        reservationRepository.deleteAllById(reservations.stream()
                .map(Reservation::getId)
                .toList());
        inventoryRepository.saveAll(inventories);
    }
}
