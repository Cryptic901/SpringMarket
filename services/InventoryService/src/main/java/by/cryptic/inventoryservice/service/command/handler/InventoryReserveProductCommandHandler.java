package by.cryptic.inventoryservice.service.command.handler;

import by.cryptic.exceptions.OutOfStockException;
import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.model.Reservation;
import by.cryptic.inventoryservice.repository.InventoryRepository;
import by.cryptic.inventoryservice.repository.ReservationRepository;
import by.cryptic.inventoryservice.service.command.InventoryReserveProductCommand;
import by.cryptic.utils.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryReserveProductCommandHandler implements CommandHandler<InventoryReserveProductCommand> {

    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public void handle(InventoryReserveProductCommand command) {
        int quantityToReserve = command.quantity();
        Inventory availableInventory = inventoryRepository.findAvailableByProductId(command.productId(), quantityToReserve)
                .orElseThrow(() -> new OutOfStockException("There are no %s in our stock".formatted(command.productId())));
        Reservation reservation = reservationRepository
                .findByReservedByProductId(command.orderId(), command.productId())
                .orElse(new Reservation());

        availableInventory.reserve(quantityToReserve);
        reservation.addQuantity(quantityToReserve);

        inventoryRepository.save(availableInventory);
        reservationRepository.save(reservation);
    }
}
