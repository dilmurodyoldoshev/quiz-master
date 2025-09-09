package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage createUser(UserDto userDto) {
        try {
            User currentAdmin = Helper.getCurrentPrincipal();

            if (userRepository.existsByUsername(userDto.getUsername())) {
                return new ResponseMessage(false, "Username already exists", null);
            }

            if (userRepository.existsByEmail(userDto.getEmail())) {
                return new ResponseMessage(false, "Email already exists", null);
            }

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

            return new ResponseMessage(true,
                    "User created successfully by admin: " + currentAdmin.getUsername(),
                    savedUser);

        } catch (Exception e) {
            return new ResponseMessage(false, "Error creating user: " + e.getMessage(), null);
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return new ResponseMessage(false, "No users found", null);
        }
        return new ResponseMessage(true, "Users retrieved successfully", users);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage getUserById(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return new ResponseMessage(false, "User not found with id: " + id, null);
        }
        return new ResponseMessage(true, "User retrieved successfully", optionalUser.get());
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage deleteUser(Integer id) {
        try {
            User currentAdmin = Helper.getCurrentPrincipal();

            if (!userRepository.existsById(id)) {
                return new ResponseMessage(false, "User not found", null);
            }

            if (currentAdmin.getId().equals(id)) {
                return new ResponseMessage(false, "Admin cannot delete himself", null);
            }

            userRepository.deleteById(id);
            return new ResponseMessage(true,
                    "User deleted successfully by admin: " + currentAdmin.getUsername(),
                    null);

        } catch (Exception e) {
            return new ResponseMessage(false, "Error deleting user: " + e.getMessage(), null);
        }
    }
}
