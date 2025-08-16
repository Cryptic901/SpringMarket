package by.cryptic.inventoryservice.service.command.handler;

import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.model.Reservation;
import by.cryptic.inventoryservice.repository.InventoryRepository;
import by.cryptic.inventoryservice.repository.ReservationRepository;
import by.cryptic.inventoryservice.service.command.InventoryReturnToStockReservedProductCommand;
import by.cryptic.utils.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryReturnToStockReservedProductCommandHandler implements CommandHandler<InventoryReturnToStockReservedProductCommand> {

    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;

    @Override
    @Transactional
    public void handle(InventoryReturnToStockReservedProductCommand command) {

        List<Reservation> reservations = reservationRepository.findByOrderId(command.orderId());

        if (reservations.isEmpty()) {
            throw new IllegalArgumentException("No reservations found for " + command.orderId());
        }

        for (Reservation reservation : reservations) {
            Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Not found inventory for product " + reservation.getProductId()));

            inventory.returnToStock(reservation.getQuantityToReserve());

            reservationRepository.delete(reservation);
            inventoryRepository.save(inventory);
        }
    }
}
