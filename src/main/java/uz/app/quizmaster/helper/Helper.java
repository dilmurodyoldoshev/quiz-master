package uz.app.quizmaster.helper;

import org.springframework.security.core.context.SecurityContextHolder;
import uz.app.quizmaster.entity.User;

public class Helper {
    public static User getCurrentPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
