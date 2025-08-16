package by.cryptic.inventoryservice.service.command.handler;

import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.model.OutboxEntity;
import by.cryptic.inventoryservice.model.Reservation;
import by.cryptic.inventoryservice.repository.InventoryRepository;
import by.cryptic.inventoryservice.repository.OutboxRepository;
import by.cryptic.inventoryservice.repository.ReservationRepository;
import by.cryptic.inventoryservice.service.command.InventoryConfirmFromReserveCommand;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.EventType;
import by.cryptic.utils.event.product.ProductUpdatedQuantityFromStockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryConfirmFromReserveCommandHandler implements CommandHandler<InventoryConfirmFromReserveCommand> {

    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;
    private final OutboxRepository outboxRepository;

    @Override
    @Transactional
    public void handle(InventoryConfirmFromReserveCommand command) {

        List<Reservation> reservations = reservationRepository.findByOrderId(command.orderId());

        if (reservations.isEmpty()) {
            throw new IllegalArgumentException("No reservations found for " + command.orderId());
        }
        for (Reservation reservation : reservations) {
            Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Not found inventory for product " + reservation.getProductId()));

            inventory.confirmFromReserve(reservation.getQuantityToReserve());

            reservationRepository.delete(reservation);
            inventoryRepository.save(inventory);
            OutboxEntity outbox = OutboxEntity.builder()
                    .aggregateId(reservation.getProductId())
                    .aggregateType("Inventory")
                    .eventType(EventType.ProductUpdatedQuantityFromStockEvent.name())
                    .payload(ProductUpdatedQuantityFromStockEvent.builder()
                            .productId(reservation.getProductId())
                            .quantity(reservation.getQuantityToReserve())
                            .build())
                    .build();
            outboxRepository.save(outbox);
        }
    }
}
