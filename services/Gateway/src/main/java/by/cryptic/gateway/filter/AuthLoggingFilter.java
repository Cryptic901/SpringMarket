package by.cryptic.gateway.filter;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthLoggingFilter implements WebFilter {

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();

        log.info("Incoming request: {} {}", method, path);

        if (path.contains("api-docs")) {
            log.warn("API-DOCS request detected: {} {}", method, path);
            log.warn("Headers: {}", exchange.getRequest().getHeaders());
        }
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .doOnNext(auth -> {
                    log.info(">>> User authenticated: {}", auth.getName());
                    log.info(">>> Authority list: {}", auth.getAuthorities().toString());
                    for (GrantedAuthority grantedAuthority : auth.getAuthorities()) {
                        log.info(">>> Authority: {}", grantedAuthority.getAuthority());
                    }
                }).then(chain.filter(exchange));
    }
}
