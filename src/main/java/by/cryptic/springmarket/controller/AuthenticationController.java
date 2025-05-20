package by.cryptic.springmarket.controller;

import by.cryptic.springmarket.dto.LoginResponseDTO;
import by.cryptic.springmarket.dto.LoginUserDTO;
import by.cryptic.springmarket.dto.RegisterUserDTO;
import by.cryptic.springmarket.dto.VerifyUserDTO;
import by.cryptic.springmarket.model.AppUser;
import by.cryptic.springmarket.service.AuthenticationService;
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

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AppUser> registerUser(
            @RequestBody @Valid RegisterUserDTO registerUserDTO) throws MessagingException {
        return ResponseEntity.ok(authenticationService.registerUser(registerUserDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(
            @RequestBody @Valid LoginUserDTO loginUserDTO) {
        return ResponseEntity.ok(authenticationService.loginUser(loginUserDTO));
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyUser(
            @RequestBody @Valid VerifyUserDTO verifyUserDTO) throws AuthenticationException {
        authenticationService.verifyUser(verifyUserDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend")
    public ResponseEntity<Void> resendVerificationMail(
            @RequestParam @Email String email) throws MessagingException, AuthenticationException {
        authenticationService.resendVerificationEmail(email);
        return ResponseEntity.ok().build();
    }
}
