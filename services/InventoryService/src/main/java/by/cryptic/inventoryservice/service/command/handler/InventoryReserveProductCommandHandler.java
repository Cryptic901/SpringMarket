package by.cryptic.inventoryservice.service.command.handler;

import by.cryptic.exceptions.OutOfStockException;
import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.model.Reservation;
import by.cryptic.inventoryservice.repository.InventoryRepository;
import by.cryptic.inventoryservice.repository.ReservationRepository;
import by.cryptic.inventoryservice.service.command.InventoryReserveProductCommand;
import by.cryptic.utils.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryReserveProductCommandHandler implements CommandHandler<InventoryReserveProductCommand> {

    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;
    private final static UUID warehouseIdDummy = UUID.randomUUID();

    @Override
    @Transactional
    public void handle(InventoryReserveProductCommand command) {
        Integer quantityToReserve = command.quantity();
        log.info(" <<<< InventoryReserveProductCommand quantityToReserve = {}", quantityToReserve);
        reserveInventory(command, quantityToReserve);
        addReservation(command, quantityToReserve);
    }

    private void reserveInventory(InventoryReserveProductCommand command, Integer quantityToReserve) {
        Inventory availableInventory = inventoryRepository.findAvailableByProductId(command.productId(), quantityToReserve)
                .orElseThrow(() -> new OutOfStockException("There are no %s in our stock".formatted(command.productId())));
        log.info("SAGA available inventory = {}", availableInventory);
        availableInventory.reserve(quantityToReserve);
        log.info("SAGA inventory after reserve = {}", availableInventory);
        inventoryRepository.save(availableInventory);
    }

    private void addReservation(InventoryReserveProductCommand command, Integer quantityToReserve) {
        log.info("quantityToReserve when adding reservation = {}", quantityToReserve);
        Reservation reservation = reservationRepository
                .findByReservedByProductId(command.orderId(), command.productId())
                .orElse(Reservation.builder()
                        .productId(command.productId())
                        .quantityToReserve(0)
                        .warehouseId(warehouseIdDummy) //TODO Add warehouse logic later
                        .orderId(command.orderId())
                        .build());

        log.info("SAGA reservation = {}", reservation);
        reservation.addQuantity(quantityToReserve);
        log.info("SAGA reservation after adding quantity = {}", reservation);
        reservationRepository.save(reservation);
    }
}
