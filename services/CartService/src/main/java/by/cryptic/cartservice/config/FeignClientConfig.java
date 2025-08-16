package by.cryptic.cartservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
@EnableFeignClients(value = "by.cryptic.cartservice.client")
@Profile("feign")
public class FeignClientConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken token) {
            String jwt = token.getToken().getTokenValue();
            requestTemplate.header("Authorization", "Bearer " + jwt);
        }
    }
}
