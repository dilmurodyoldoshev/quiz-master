package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.quizmaster.dto.UserDto;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.enums.Role;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // faqat ADMIN yangi student yoki teacher qo‘shishi mumkin
    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createUser(@RequestBody UserDto userDto) {
        ResponseMessage response = userService.createUser(userDto);
        return ResponseEntity.ok(response);
    }

    // barcha foydalanuvchilar ro‘yxati
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // id bo‘yicha foydalanuvchi olish
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // foydalanuvchini o‘chirish
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable Integer id) {
        ResponseMessage response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }
}
