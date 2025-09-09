package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.UserDto;
import uz.app.quizmaster.payload.ResponseMessage;

public interface UserService {
    ResponseMessage createUser(UserDto userDto);
    ResponseMessage getAllUsers();
    ResponseMessage getUserById(Integer id);
    ResponseMessage deleteUser(Integer id);
}
