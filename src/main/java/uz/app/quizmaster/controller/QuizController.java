package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.service.QuestionService;
import uz.app.quizmaster.service.QuizService;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final QuestionService questionService;

    // Barcha quizlarni olish - hammaga ko'rinadi
    @GetMapping
    public ResponseEntity<ResponseMessage> getAllQuizzes() {
        ResponseMessage response = quizService.getAllQuizzesPublic();
        return ResponseEntity.ok(response);
    }

    // Quizni id bo‘yicha olish - hammaga ko'rinadi
    @GetMapping("/{quizId}")
    public ResponseEntity<ResponseMessage> getQuizById(@PathVariable Integer quizId) {
        ResponseMessage response = quizService.getQuizByIdPublic(quizId);
        return ResponseEntity.ok(response);
    }

    // Quizdagi barcha savollarni olish - hammaga ko'rinadi
    @GetMapping("/{quizId}/questions")
    public ResponseEntity<ResponseMessage> getAllQuestions(@PathVariable Integer quizId) {
        ResponseMessage response = questionService.getAllQuestionsPublic(quizId);
        return ResponseEntity.ok(response);
    }

    // Bitta savolni olish - hammaga ko'rinadi
    @GetMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<ResponseMessage> getQuestion(@PathVariable Integer quizId,
                                                       @PathVariable Integer questionId) {
        ResponseMessage response = questionService.getQuestionPublic(quizId, questionId);
        return ResponseEntity.ok(response);
    }
}
