package by.cryptic.userservice.repository.write;

import by.cryptic.userservice.model.write.AppUser;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    @Transactional
    @Modifying
    @Query("delete from AppUser u where u.id = :id")
    long deleteByUserIdReturningCount(@Param("id") UUID id);
}
