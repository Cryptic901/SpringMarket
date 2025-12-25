package by.cryptic.productservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
@Slf4j
@EnableFeignClients(value = "by.cryptic.productservice.client")
@ConditionalOnProperty(name = "spring.feign.enabled", havingValue = "true")
public class FeignClientConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication in FeignClientConfig: {}", auth);
        if (auth instanceof JwtAuthenticationToken token) {
            String jwt = token.getToken().getTokenValue();
            requestTemplate.header("Authorization", "Bearer " + jwt);
        }
    }
}
