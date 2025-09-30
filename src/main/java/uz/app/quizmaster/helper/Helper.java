package uz.app.quizmaster.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.app.quizmaster.entity.Question;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.QuestionRepository;

@Component
public class Helper {

    private static QuestionRepository questionRepository;

    @Autowired
    public Helper(QuestionRepository questionRepository) {
        Helper.questionRepository = questionRepository;
    }

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

    // Questionni ID bo'yicha olish
    public static Question getQuestionById(Integer questionId) {
        if (questionRepository == null) {
            throw new IllegalStateException("QuestionRepository not initialized");
        }
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));
    }
}
