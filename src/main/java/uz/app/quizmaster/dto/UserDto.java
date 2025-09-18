package uz.app.quizmaster.dto;

import lombok.Data;
import uz.app.quizmaster.enums.Role;

@Data
public class UserDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String email;
    private String password;
    private Role role; // faqat student yoki teacher qoshilishi mumkin
}
