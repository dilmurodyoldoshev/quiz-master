package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.UserDto;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.payload.ResponseMessage;

import java.util.List;

public interface UserService {
    ResponseMessage<User> createUser(UserDto userDto);
    ResponseMessage<List<User>> getAllUsers();
    ResponseMessage<User> getUserById(Integer id);
}

