package uz.app.quizmaster.dto;

import lombok.Data;

@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String email;
    private String password;
}
