package by.cryptic.gateway.filter;

import by.cryptic.security.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
@Order(-1)
@Slf4j
public class JwtSecurityFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    @Value("${spring.security.cookie.name}")
    private String cookieName;

    private Mono<Void> handleJWTException(String message, ServerWebExchange webExchange, int status) {
        ServerHttpResponse response = webExchange.getResponse();
        response.setRawStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String json = "{\"error\": \"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private String parseToken(ServerWebExchange exchange) {
        HttpHeaders authHeader = exchange.getRequest().getHeaders();
        String authHeaderValue = authHeader.getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeaderValue != null && authHeaderValue.startsWith("Bearer ")) {

            log.info("JwtSecurityFilter | parseToken | Bearer token found : {}", authHeaderValue.substring(7));

            return authHeaderValue.substring(7);
        }
        var cookies = exchange.getRequest().getCookies();
        if (cookies.containsKey(cookieName)) {
            HttpCookie cookie = cookies.getFirst(cookieName);
            if (cookie != null) {
                log.info("JwtSecurityFilter | parseToken | Authorization cookie found : {}", cookieName);
                return cookie.getValue();
            }
        }
        log.error("JwtSecurityFilter | parseToken | Bearer token not found");
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = String.valueOf(exchange.getRequest().getPath());
        log.info("Path: {}", path);
        if (path.startsWith("/api/v1/auth")) {
            return chain.filter(exchange);
        }

        String token = parseToken(exchange);

        if (token == null || token.isEmpty()) {
            log.error("JwtSecurityFilter | doFilterInternal | Token is null or empty");
            return chain.filter(exchange);
        }
        try {
            log.debug("JwtSecurityFilter | doFilterInternal | JWT token: {}", token);
            String username = jwtUtil.extractUsername(token);
            if (username != null) {
                if (!jwtUtil.isTokenValid(token)) {
                    return handleJWTException("Invalid or expired JWT Token", exchange, HttpStatus.SC_UNAUTHORIZED);
                }
            }
            return chain.filter(exchange.mutate()
                    .request(builder ->
                            builder.header("X-User-ID", jwtUtil.extractId(token))
                                    .header("X-User-Name", jwtUtil.extractUsername(token))
                                    .header("X-User-Email", jwtUtil.extractEmail(token))
                                    .header("X-Role", String.valueOf(jwtUtil.extractRole(token))))
                    .build());

        } catch (ExpiredJwtException e) {
            log.warn("JwtSecurityFilter | doFilterInternal | JWT token is expired: {}", e.getMessage());
            return handleJWTException("JWT token has expired. Please log in again", exchange,
                    HttpStatus.SC_UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JwtSecurityFilter | doFilterInternal | JWT token is invalid: {}", e.getMessage());
            return handleJWTException("JWT token is invalid", exchange, HttpStatus.SC_UNAUTHORIZED);
        } catch (Exception e) {
            log.warn("JwtSecurityFilter | doFilterInternal | Unknown error: {}", e.getMessage());
            return handleJWTException("Unknown error", exchange, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
