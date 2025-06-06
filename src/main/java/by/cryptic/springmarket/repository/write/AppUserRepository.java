package by.cryptic.springmarket.repository.write;

import by.cryptic.springmarket.model.write.AppUser;
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
    Optional<AppUser> findByIdWithCart(@Param("id") UUID id);

    @Query("SELECT u from AppUser u join fetch u.cart where u.username = :username")
    Optional<AppUser> findByUsernameWithCart(@Param("username") String username);
}
