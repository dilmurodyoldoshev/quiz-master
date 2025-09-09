package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.service.QuestionService;
import uz.app.quizmaster.service.QuizService;

import static uz.app.quizmaster.helper.Helper.buildResponse;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final QuestionService questionService;

    // Barcha quizlarni olish - hammaga ko‘rinadi
    @GetMapping
    public ResponseEntity<ResponseMessage> getAllQuizzes() {
        return buildResponse(quizService.getAllQuizzesPublic());
    }

    // Quizni id bo‘yicha olish - hammaga ko‘rinadi
    @GetMapping("/{quizId}")
    public ResponseEntity<ResponseMessage> getQuizById(@PathVariable Integer quizId) {
        return buildResponse(quizService.getQuizByIdPublic(quizId));
    }

    // Quizdagi barcha savollarni olish - hammaga ko‘rinadi
    @GetMapping("/{quizId}/questions")
    public ResponseEntity<ResponseMessage> getAllQuestions(@PathVariable Integer quizId) {
        return buildResponse(questionService.getAllQuestionsPublic(quizId));
    }

    // Bitta savolni olish - hammaga ko‘rinadi
    @GetMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<ResponseMessage> getQuestion(
            @PathVariable Integer quizId,
            @PathVariable Integer questionId
    ) {
        return buildResponse(questionService.getQuestionPublic(quizId, questionId));
    }
}
