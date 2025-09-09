package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.quizmaster.dto.LoginDto;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.service.AuthService;

import static uz.app.quizmaster.helper.Helper.buildResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> login(@RequestBody LoginDto loginDto) {
        return buildResponse(authService.login(loginDto));
    }
}
