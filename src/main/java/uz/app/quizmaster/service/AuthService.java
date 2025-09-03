package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.LoginDto;
import uz.app.quizmaster.payload.ResponseMessage;

public interface AuthService {
    ResponseMessage login(LoginDto loginDto);
}
