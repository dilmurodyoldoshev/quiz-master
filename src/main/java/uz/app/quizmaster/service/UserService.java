package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.UserDto;
import uz.app.quizmaster.payload.ResponseMessage;

import java.util.List;

public interface UserService {
    ResponseMessage<UserDto> createUser(UserDto userDto);
    ResponseMessage<List<UserDto>> getAllUsers();
    ResponseMessage<UserDto> getUserById(Integer id);
    ResponseMessage<UserDto> getMyProfile();
}
