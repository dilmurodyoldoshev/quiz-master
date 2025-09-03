package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.dto.LoginDto;
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

    public ResponseMessage login(LoginDto loginDto) {

        // 1️⃣ Foydalanuvchini username yoki email orqali topish
        Optional<User> optionalUser = userRepository.findByUsernameOrEmail(
                loginDto.getUsernameOrEmail(),
                loginDto.getUsernameOrEmail()
        );

        if (optionalUser.isEmpty()) {
            return new ResponseMessage(false, "User not found", loginDto.getUsernameOrEmail());
        }

        User user = optionalUser.get();

        // 2️⃣ Parolni tekshirish
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return new ResponseMessage(false, "Password incorrect", loginDto.getUsernameOrEmail());
        }

        // 3️⃣ JWT token yaratish
        String token = jwtProvider.generateToken(user.getEmail(), user.getRole().name());

        // 4️⃣ SecurityContext ga user qo‘yish (filter orqali)
        myFilter.setUserToContext(user.getEmail());

        // 5️⃣ Response qaytarish
        return new ResponseMessage(true, "User logged in", token);
    }
}
