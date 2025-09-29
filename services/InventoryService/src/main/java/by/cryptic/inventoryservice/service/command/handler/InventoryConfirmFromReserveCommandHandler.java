package by.cryptic.inventoryservice.service.command.handler;

import by.cryptic.inventoryservice.model.Inventory;
import by.cryptic.inventoryservice.model.Reservation;
import by.cryptic.inventoryservice.publisher.InventoryEventPublisher;
import by.cryptic.inventoryservice.repository.InventoryRepository;
import by.cryptic.inventoryservice.repository.ReservationRepository;
import by.cryptic.inventoryservice.service.command.InventoryConfirmFromReserveCommand;
import by.cryptic.utils.handler.CommandHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryConfirmFromReserveCommandHandler implements CommandHandler<InventoryConfirmFromReserveCommand> {

    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;
    private final InventoryEventPublisher inventoryEventPublisher;

    @Override
    @Transactional
    public void handle(InventoryConfirmFromReserveCommand command) {
        List<Reservation> reservations = getListOfReservationsAndCheckIsNotEmpty(command);
        log.info("Inventory Confirm From Reserve Command {}", reservations);
        List<Inventory> inventories = new ArrayList<>();

        for (Reservation reservation : reservations) {
            confirmFromReserve(reservation, inventories);
        }
        reservationRepository.deleteAllById(reservations.stream()
                .map(Reservation::getId)
                .toList());
        inventoryRepository.saveAll(inventories);
    }

    private List<Reservation> getListOfReservationsAndCheckIsNotEmpty(InventoryConfirmFromReserveCommand command) {
        List<Reservation> reservations = reservationRepository.findAllByOrderId(command.orderId());
        if (reservations.isEmpty()) {
            throw new IllegalArgumentException("No reservations found for " + command.orderId());
        }
        return reservations;
    }

    private void confirmFromReserve(Reservation reservation, List<Inventory> inventories) {
        Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found inventory for product " + reservation.getProductId()));

        inventories.add(inventory);
        log.info("Inventories after confirmation {}", inventories);
        inventoryEventPublisher.updateProductQuantity(inventory);
    }
}
