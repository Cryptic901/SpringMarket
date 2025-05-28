package by.cryptic.springmarket.repository;

import by.cryptic.springmarket.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    @Query("SELECT u from AppUser u join fetch u.cart where u.id = :id")
    AppUser findByIdWithCart(@Param("id") UUID id);
}
