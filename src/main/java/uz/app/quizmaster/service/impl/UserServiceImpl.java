package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.dto.UserDto;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.enums.Role;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.UserRepository;
import uz.app.quizmaster.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseMessage createUser(UserDto userDto) {
        // username yoki email oldindan mavjudligini tekshiramiz
        if (userRepository.existsByUsername(userDto.getUsername())) {
            return new ResponseMessage(false, "Username already exists", null);
        }

        // faqat STUDENT yoki TEACHER qo‘shish mumkin
        if (!(userDto.getRole() == Role.STUDENT || userDto.getRole() == Role.TEACHER)) {
            return new ResponseMessage(false, "Only STUDENT or TEACHER can be created by Admin", null);
        }

        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setPhone(userDto.getPhone());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());

        User savedUser = userRepository.save(user);
        return new ResponseMessage(true, "User created successfully", savedUser);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public ResponseMessage deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            return new ResponseMessage(false, "User not found", null);
        }
        userRepository.deleteById(id);
        return new ResponseMessage(true, "User deleted successfully", null);
    }
}
