package by.cryptic.orderservice.repository.read;

import by.cryptic.orderservice.model.read.CustomerOrderView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderViewRepository extends MongoRepository<CustomerOrderView, UUID> {
}
