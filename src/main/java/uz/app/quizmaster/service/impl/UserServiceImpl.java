package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.dto.UserDto;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.enums.Role;
import uz.app.quizmaster.helper.Helper;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.UserRepository;
import uz.app.quizmaster.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<User> createUser(UserDto userDto) {
        try {
            User currentAdmin = Helper.getCurrentPrincipal();

            if (userRepository.existsByUsername(userDto.getUsername())) {
                return ResponseMessage.fail("Username already exists", null);
            }

            if (userRepository.existsByEmail(userDto.getEmail())) {
                return ResponseMessage.fail("Email already exists", null);
            }

            if (!(userDto.getRole() == Role.STUDENT || userDto.getRole() == Role.TEACHER)) {
                return ResponseMessage.fail("Only STUDENT or TEACHER can be created by Admin", null);
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

            return ResponseMessage.success(
                    "User created successfully by admin: " + currentAdmin.getUsername(),
                    savedUser
            );

        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error creating user", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseMessage.fail("No users found", null);
        }
        return ResponseMessage.success("Users retrieved successfully", users);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<User> getUserById(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseMessage.fail("User not found with id: " + id, null);
        }
        return ResponseMessage.success("User retrieved successfully", optionalUser.get());
    }
}
