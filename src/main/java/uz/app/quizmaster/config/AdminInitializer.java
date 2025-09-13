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
        // Admin mavjud bo‘lmasa, qo‘shiladi
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Master");
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("root123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        // 15 ta student yaratish
        for (int i = 1; i <= 15; i++) {
            String username = "student" + i;
            if (!userRepository.existsByUsername(username)) {
                User student = new User();
                student.setFirstName("Student" + i);
                student.setLastName("Test");
                student.setUsername(username);
                student.setEmail(username + "@test.com");
                student.setPhone("90000000" + i);
                student.setPassword(passwordEncoder.encode("123456"));
                student.setRole(Role.STUDENT);
                userRepository.save(student);
            }
        }

        // 5 ta teacher yaratish
        for (int i = 1; i <= 5; i++) {
            String username = "teacher" + i;
            if (!userRepository.existsByUsername(username)) {
                User teacher = new User();
                teacher.setFirstName("Teacher" + i);
                teacher.setLastName("Test");
                teacher.setUsername(username);
                teacher.setEmail(username + "@test.com");
                teacher.setPhone("91000000" + i);
                teacher.setPassword(passwordEncoder.encode("123456"));
                teacher.setRole(Role.TEACHER);
                userRepository.save(teacher);
            }
        }
    }
}
