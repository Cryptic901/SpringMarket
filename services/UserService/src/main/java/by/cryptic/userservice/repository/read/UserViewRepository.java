package by.cryptic.userservice.repository.read;

import by.cryptic.userservice.model.read.AppUserView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserViewRepository extends MongoRepository<AppUserView, UUID> {
}
