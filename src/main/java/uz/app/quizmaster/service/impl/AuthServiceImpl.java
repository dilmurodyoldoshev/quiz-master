package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.dto.LoginDto;
import uz.app.quizmaster.dto.LoginResponse;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.UserRepository;
import uz.app.quizmaster.security.JwtProvider;
import uz.app.quizmaster.security.MyFilter;
import uz.app.quizmaster.service.AuthService;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public ResponseMessage<LoginResponse> login(LoginDto loginDto) {
        try {
            User user = userRepository.findByUsernameOrEmail(
                    loginDto.getUsernameOrEmail(),
                    loginDto.getUsernameOrEmail()
            ).orElse(null);

            if (user == null) {
                return ResponseMessage.fail("User not found", null);
            }

            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                return ResponseMessage.fail("Invalid credentials", null);
            }

            String token = jwtProvider.generateToken(user.getEmail(), user.getRole().name());
            LoginResponse loginResponse = new LoginResponse(token, user.getRole().name());

            return ResponseMessage.success("Login successful", loginResponse);

        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e);
            return ResponseMessage.fail("An error occurred", null);
        }
    }

}
