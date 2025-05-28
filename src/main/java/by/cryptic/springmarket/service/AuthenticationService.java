package by.cryptic.springmarket.service;

import by.cryptic.springmarket.dto.LoginResponseDTO;
import by.cryptic.springmarket.dto.LoginUserDTO;
import by.cryptic.springmarket.dto.RegisterUserDTO;
import by.cryptic.springmarket.dto.VerifyUserDTO;
import by.cryptic.springmarket.enums.Role;
import by.cryptic.springmarket.model.AppUser;
import by.cryptic.springmarket.model.Cart;
import by.cryptic.springmarket.repository.AppUserRepository;
import by.cryptic.springmarket.util.JwtUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AppUserDetailsService userDetailsService;
    private final EmailContentBuilder emailContentBuilder;

    public AppUser registerUser(RegisterUserDTO registerUserDto) throws MessagingException {
        AppUser user = new AppUser(registerUserDto.getUsername(), registerUserDto.getEmail(),
                passwordEncoder.encode(registerUserDto.getPassword()),
                Role.ROLE_USER, registerUserDto.getPhoneNumber(), registerUserDto.getGender());
        Cart cart = Cart.builder()
                .user(user)
                .build();
        user.setCart(cart);
        user.setVerifyCode(generateRandomSixNumber());
        user.setVerificationCodeExpiresAt(OffsetDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    public LoginResponseDTO loginUser(LoginUserDTO loginUserDto) {
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

    public void verifyUser(VerifyUserDTO verifyUserDto) throws AuthenticationException {
        Optional<AppUser> optionalUser = userRepository.findByEmail(verifyUserDto.email());
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(OffsetDateTime.now())) {
                throw new AuthenticationException("Verification has expired. Please register again.");
            }
            if (user.getVerifyCode().equals(verifyUserDto.verificationCode())) {
                user.setEnabled(true);
                user.setVerificationCodeExpiresAt(null);
                user.setVerifyCode(null);
                userRepository.save(user);
            } else {
                throw new AuthenticationException("Invalid verification code.");
            }
        } else {
            throw new AuthenticationException(String.format("User with email %s not found", verifyUserDto.email()));
        }
    }

    public void resendVerificationEmail(String email) throws MessagingException, AuthenticationException {
        Optional<AppUser> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            if (user.isEnabled()) {
                throw new AuthenticationException("Account already verified");
            }
            user.setVerifyCode(generateRandomSixNumber());
            user.setVerificationCodeExpiresAt(OffsetDateTime.now().plusMinutes(15));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new AuthenticationException(String.format("User with email %s not found", email));
        }
    }

    public void sendVerificationEmail(AppUser user) throws MessagingException {
        emailService.sendEmail(user.getEmail(), "SpringMarket verification",
                emailContentBuilder.buildVerificationEmailContent(user.getVerifyCode()));
    }

    private int generateRandomSixNumber() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }
}
