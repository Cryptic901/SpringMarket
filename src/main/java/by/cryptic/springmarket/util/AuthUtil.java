package by.cryptic.springmarket.util;

import by.cryptic.springmarket.model.write.AppUser;
import by.cryptic.springmarket.repository.write.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AuthUtil {

    private final AppUserRepository appUserRepository;

    public AuthUtil(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public int generateRandomSixNumber() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

    public AppUser getUserFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with name %s".formatted(username)));
    }
}
