package by.cryptic.springmarket.service.query.handler.user;

import by.cryptic.springmarket.dto.LoginResponseDTO;
import by.cryptic.springmarket.dto.LoginUserDTO;
import by.cryptic.springmarket.service.AppUserDetailsService;
import by.cryptic.springmarket.service.query.handler.QueryHandler;
import by.cryptic.springmarket.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginQueryHandler implements QueryHandler<LoginUserDTO, LoginResponseDTO> {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AppUserDetailsService userDetailsService;

    public LoginResponseDTO handle(LoginUserDTO loginUserDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.username(),
                        loginUserDto.password())
        );

        return new LoginResponseDTO(
                jwtUtil.generateToken(userDetailsService.loadUserByUsername(loginUserDto.username())),
                jwtUtil.getExpiration()
        );
    }
}
