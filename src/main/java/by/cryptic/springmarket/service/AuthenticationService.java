package by.cryptic.springmarket.service;

import by.cryptic.springmarket.dto.LoginResponseDTO;
import by.cryptic.springmarket.dto.LoginUserDTO;
import by.cryptic.springmarket.dto.RegisterUserDTO;
import by.cryptic.springmarket.dto.VerifyUserDTO;
import by.cryptic.springmarket.enums.Role;
import by.cryptic.springmarket.model.AppUser;
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

    public AppUser registerUser(RegisterUserDTO registerUserDto) throws MessagingException {
        AppUser user = new AppUser(registerUserDto.getUsername(), registerUserDto.getEmail(),
                passwordEncoder.encode(registerUserDto.getPassword()),
                Role.ROLE_USER, registerUserDto.getPhoneNumber(), registerUserDto.getGender());
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
        emailService.sendEmail(user.getEmail(), "SpringMarket verification", "<!DOCTYPE html>\n" +
                "<html lang=ru>\n" +
                "<head>\n" +
                "    <meta charset=\\UTF-8\\>\n" +
                "    <title>Подтверждение регистрации</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f6f6f6;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "\n" +
                "        .email-container {\n" +
                "            max-width: 500px;\n" +
                "            margin: auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            padding: 30px;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "\n" +
                "        .code {\n" +
                "            font-size: 32px;\n" +
                "            letter-spacing: 10px;\n" +
                "            font-weight: bold;\n" +
                "            color: #2c3e50;\n" +
                "            text-align: center;\n" +
                "            margin: 30px 0;\n" +
                "        }\n" +
                "\n" +
                "        .footer {\n" +
                "            font-size: 12px;\n" +
                "            color: #777;\n" +
                "            text-align: center;\n" +
                "            margin-top: 40px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=email-container> \n" +
                "    <h2>Приветствуем!</h2> \n" +
                "    <p>Вы зарегистрировались на нашем сайте. Для завершения регистрации введите код подтверждения:</p>\n" +
                "    <div class=code>" + user.getVerifyCode() + " </div>\n" +
                "    <p>Если вы не регистрировались, просто проигнорируйте это письмо.</p>\n" +
                "    <div class=footer>\n" +
                "        &copy; 2025 Ваш SpringMarket. Все права защищены.\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>");
    }

    public int generateRandomSixNumber() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }
}
