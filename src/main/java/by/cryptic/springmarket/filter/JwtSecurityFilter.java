package by.cryptic.springmarket.filter;

import by.cryptic.springmarket.service.AppUserDetailsService;
import by.cryptic.springmarket.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AppUserDetailsService appUserDetailsService;
    private final ObjectMapper objectMapper;

    @Value("${spring.security.cookie.name}")
    private String cookieName;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        log.info("Path: {}", path);
        if (path.equals("/api/v1/auth/login") ||
                path.equals("/api/v1/auth/register") ||
                path.equals("/api/v1/auth/verify")
        ) {
            filterChain.doFilter(request, response);
            return;
        }


        String token = parseToken(request);

        if (token == null || token.isEmpty()) {
            log.info("JwtSecurityFilter | doFilterInternal | Token is null or empty");
            filterChain.doFilter(request, response);
            return;
        }
        try {
            log.debug("JwtSecurityFilter | doFilterInternal | JWT token: {}", token);
            String username = jwtUtil.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = appUserDetailsService.loadUserByUsername(username);

                if (jwtUtil.isTokenValid(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    log.debug("JwtSecurityFilter | doFilterInternal | JWT token is valid. Setting authentication for user: {}", username);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.warn("JwtSecurityFilter | doFilterInternal | JWT token is expired: {}", e.getMessage());
            handleJWTException("JWT token has expired. Please log in again", response,
                    HttpServletResponse.SC_UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JwtSecurityFilter | doFilterInternal | JWT token is invalid: {}", e.getMessage());
            handleJWTException("JWT token is invalid", response, HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            log.warn("JwtSecurityFilter | doFilterInternal | Unknown error: {}", e.getMessage());
            handleJWTException("Unknown error", response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private void handleJWTException(String message, HttpServletResponse response, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(message));
    }

    private String parseToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            log.info("JwtSecurityFilter | parseToken | Bearer token found : {}", authHeader.substring(7));

            return authHeader.substring(7);
        }
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    log.info("JwtSecurityFilter | parseToken | Authorization cookie found : {}", cookieName);
                    return cookie.getValue();
                }
            }
        }
        log.error("JwtSecurityFilter | parseToken | Bearer token not found");
        return null;
    }
}
