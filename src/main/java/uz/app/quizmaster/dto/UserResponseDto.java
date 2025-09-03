package uz.app.quizmaster.dto;

import lombok.Data;
import uz.app.quizmaster.enums.Role;

@Data
public class UserResponseDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String email;
    private Role role;
}
