package by.cryptic.orderservice.repository.read;

import by.cryptic.orderservice.model.read.CustomerOrderView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderViewRepository extends JpaRepository<CustomerOrderView, UUID> {
}
