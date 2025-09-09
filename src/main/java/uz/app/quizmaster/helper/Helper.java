package uz.app.quizmaster.helper;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.payload.ResponseMessage;

public class Helper {

    // Hozirgi login qilgan userni olish
    public static User getCurrentPrincipal() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // ResponseEntity build qilish (200 yoki 400 qaytaradi)
    public static ResponseEntity<ResponseMessage> buildResponse(ResponseMessage response) {
        return response.success()
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }
}
