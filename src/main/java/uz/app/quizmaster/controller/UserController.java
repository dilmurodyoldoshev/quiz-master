package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.quizmaster.dto.UserDto;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.service.UserService;

import static uz.app.quizmaster.helper.Helper.buildResponse;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // faqat ADMIN yangi student yoki teacher qo‘shishi mumkin
    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createUser(@RequestBody UserDto userDto) {
        return buildResponse(userService.createUser(userDto));
    }

    // barcha foydalanuvchilar ro‘yxati
    @GetMapping
    public ResponseEntity<ResponseMessage> getAllUsers() {
        return buildResponse(userService.getAllUsers());
    }

    // id bo‘yicha foydalanuvchi olish
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getUserById(@PathVariable Integer id) {
        return buildResponse(userService.getUserById(id));
    }

}
