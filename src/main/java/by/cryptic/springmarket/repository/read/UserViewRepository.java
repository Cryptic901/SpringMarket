package by.cryptic.springmarket.repository.read;

import by.cryptic.springmarket.model.read.AppUserView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserViewRepository extends JpaRepository<AppUserView, UUID> {
}
