package uz.app.quizmaster.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.enums.Role;
import uz.app.quizmaster.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) { // yoki admin mavjudligini tekshirish
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Master");
            admin.setUsername("admin");
                admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("root123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }
    }
}
