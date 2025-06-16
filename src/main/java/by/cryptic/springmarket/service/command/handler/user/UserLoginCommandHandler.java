package by.cryptic.springmarket.service.command.handler.user;

import by.cryptic.springmarket.dto.LoginUserDTO;
import by.cryptic.springmarket.event.user.UserLoginedEvent;
import by.cryptic.springmarket.service.command.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginCommandHandler implements CommandHandler<LoginUserDTO> {

    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;

    public void handle(LoginUserDTO loginUserDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.username(),
                        loginUserDto.password())
        );
        eventPublisher.publishEvent(new UserLoginedEvent
                (loginUserDto.username(), loginUserDto.password()));
        log.info("User {} logged in ", loginUserDto.username() + " sent UserLoginedEvent");
    }
}
