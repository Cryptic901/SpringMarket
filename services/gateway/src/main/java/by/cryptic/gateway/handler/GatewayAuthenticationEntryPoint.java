package by.cryptic.gateway.handler;

import by.cryptic.utils.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        var response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.empty();
        }

        int status = 401;
        String message = "Authentication failed";

        if (ex instanceof AuthenticationServiceException) {
            status = 503;
            message = "Authentication service temporarily unavailable";
            log.error("Authentication service unavailable: {}", ex.getMessage(), ex);

        } else if (ex instanceof BadCredentialsException) {
            if (ex.getMessage() != null) {
                if (ex.getMessage().contains("expired")) {
                    message = "Token has expired";
                    log.warn("Expired token from: {}", exchange.getRequest().getRemoteAddress());

                } else if (ex.getMessage().contains("signature")) {
                    message = "Invalid token signature";
                    log.warn("Invalid signature from: {}", exchange.getRequest().getRemoteAddress());

                } else {
                    message = "Invalid authentication token";
                    log.warn("Invalid token: {}", ex.getMessage());
                }
            }

        } else {
            // Другие ошибки аутентификации
            log.warn("Authentication failed: {} - {}",
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            );
        }

        response.setStatusCode(HttpStatusCode.valueOf(status));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse = new ErrorResponse(
                message,
                status,
                exchange.getRequest().getPath().value()
        );

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(errorResponse);
        } catch (Exception e) {
            log.error("Failed to serialize error response", e);
            String fallback = String.format(
                    "{\"message\":\"%s\",\"status\":%d,\"path\":\"%s\"}",
                    message, status, exchange.getRequest().getPath().value()
            );
            bytes = fallback.getBytes(StandardCharsets.UTF_8);
        }

        var buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}

