package by.cryptic.springmarket.config;

import by.cryptic.springmarket.model.write.AppUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public class AuditAware implements AuditorAware<UUID> {
    @Override
    @NonNull
    public Optional<UUID> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        Object user = auth.getPrincipal();
        if (!(user instanceof AppUser userDetails)) {
            return Optional.empty();
        }
        return Optional.of(userDetails.getId());
    }
}
