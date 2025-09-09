package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final MyFilter myFilter;

    @Override
    public ResponseMessage login(LoginDto loginDto) {
        try {
            Optional<User> optionalUser = userRepository.findByUsernameOrEmail(
                    loginDto.getUsernameOrEmail(),
                    loginDto.getUsernameOrEmail()
            );

            if (optionalUser.isEmpty()) {
                return new ResponseMessage(false, "User not found", null);
            }

            User user = optionalUser.get();

            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                return new ResponseMessage(false, "Invalid credentials", null);
            }

            // JWT token yaratish
            String token = jwtProvider.generateToken(user.getEmail(), user.getRole().name());

            // SecurityContext ga user qo‘yish
            myFilter.setUserToContext(user.getEmail());

            // Role bilan javob
            LoginResponse loginResponse = new LoginResponse(token, user.getRole().name());

            return new ResponseMessage(true, "Login successful", loginResponse);

        } catch (Exception e) {
            return new ResponseMessage(false, "An error occurred: " + e.getMessage(), null);
        }
    }
}
