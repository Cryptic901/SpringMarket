package by.cryptic.authenticationservice.controller;

import by.cryptic.authenticationservice.dto.LoginResponseDTO;
import by.cryptic.authenticationservice.dto.LoginUserDTO;
import by.cryptic.authenticationservice.dto.VerifyUserDTO;
import by.cryptic.authenticationservice.model.AuthUser;
import by.cryptic.authenticationservice.service.AppUserDetailsService;
import by.cryptic.authenticationservice.service.command.UserRegisterCommand;
import by.cryptic.authenticationservice.service.command.handler.UserLoginCommandHandler;
import by.cryptic.authenticationservice.service.command.handler.UserRegisterCommandHandler;
import by.cryptic.authenticationservice.service.command.handler.UserResendVerifyCommandHandler;
import by.cryptic.authenticationservice.service.command.handler.UserVerifyCommandHandler;
import by.cryptic.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.SameSiteCookies;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserRegisterCommandHandler userRegisterCommandHandler;
    private final UserLoginCommandHandler userLoginQueryHandler;
    private final UserVerifyCommandHandler userVerifyCommandHandler;
    private final UserResendVerifyCommandHandler userResendVerifyCommandHandler;
    private final AppUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    @Value("${spring.security.cookie.name}")
    private String cookieName;
    @Value("${spring.security.cookie.expiration}")
    private Duration cookieMaxAge;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(
            @RequestBody @Valid UserRegisterCommand registerUserDTO) {
        userRegisterCommandHandler.handle(registerUserDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> loginUser(
            @RequestBody @Valid LoginUserDTO loginUserDTO, HttpServletResponse response) {

        userLoginQueryHandler.handle(loginUserDTO);
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginUserDTO.username());
        AuthUser user = (AuthUser) userDetails;
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                jwtUtil.generateToken(userDetails, user.getId(), user.getEmail()), jwtUtil.getExpiration());

        ResponseCookie cookie = ResponseCookie.from(cookieName, loginResponseDTO.token())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite(SameSiteCookies.STRICT.toString())
                .maxAge(cookieMaxAge)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyUser(
            @RequestBody @Valid VerifyUserDTO verifyUserDTO) throws AuthenticationException {
        userVerifyCommandHandler.handle(verifyUserDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend")
    public ResponseEntity<Void> resendVerificationMail(
            @RequestParam @Email String email) throws AuthenticationException {
        userResendVerifyCommandHandler.handle(email);
        return ResponseEntity.ok().build();
    }
}
