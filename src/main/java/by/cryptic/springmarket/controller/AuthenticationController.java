package by.cryptic.springmarket.controller;

import by.cryptic.springmarket.dto.LoginResponseDTO;
import by.cryptic.springmarket.dto.LoginUserDTO;
import by.cryptic.springmarket.dto.VerifyUserDTO;
import by.cryptic.springmarket.service.command.UserRegisterCommand;
import by.cryptic.springmarket.service.command.handler.user.UserRegisterCommandHandler;
import by.cryptic.springmarket.service.command.handler.user.UserResendVerifyCommandHandler;
import by.cryptic.springmarket.service.command.handler.user.UserVerifyCommandHandler;
import by.cryptic.springmarket.service.query.handler.user.UserLoginQueryHandler;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final UserRegisterCommandHandler userRegisterCommandHandler;
    private final UserLoginQueryHandler userLoginQueryHandler;
    private final UserVerifyCommandHandler userVerifyCommandHandler;
    private final UserResendVerifyCommandHandler userResendVerifyCommandHandler;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(
            @RequestBody @Valid UserRegisterCommand registerUserDTO) {
        userRegisterCommandHandler.handle(registerUserDTO);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(
            @RequestBody @Valid LoginUserDTO loginUserDTO) {
        return ResponseEntity.ok(userLoginQueryHandler.handle(loginUserDTO));
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyUser(
            @RequestBody @Valid VerifyUserDTO verifyUserDTO) throws AuthenticationException {
        userVerifyCommandHandler.handle(verifyUserDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend")
    public ResponseEntity<Void> resendVerificationMail(
            @RequestParam @Email String email) throws MessagingException, AuthenticationException {
        userResendVerifyCommandHandler.handle(email);
        return ResponseEntity.ok().build();
    }
}
