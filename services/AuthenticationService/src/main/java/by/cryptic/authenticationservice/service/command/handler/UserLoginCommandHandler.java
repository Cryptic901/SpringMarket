package by.cryptic.authenticationservice.service.command.handler;

import by.cryptic.authenticationservice.dto.LoginUserDTO;
import by.cryptic.utils.CommandHandler;
import by.cryptic.utils.event.user.UserLoginedEvent;
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
