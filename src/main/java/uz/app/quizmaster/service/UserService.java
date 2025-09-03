package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.UserDto;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.payload.ResponseMessage;

import java.util.List;

public interface UserService {
    ResponseMessage createUser(UserDto userDto);
    List<User> getAllUsers();
    User getUserById(Integer id);
    ResponseMessage deleteUser(Integer id);
}
