package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.quizmaster.entity.Answer;
import uz.app.quizmaster.entity.Attempt;
import uz.app.quizmaster.entity.Result;
import uz.app.quizmaster.service.AnswerService;
import uz.app.quizmaster.service.AttemptService;
import uz.app.quizmaster.service.ResultService;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final AttemptService attemptService;
    private final AnswerService answerService;
    private final ResultService resultService;

    // Quizni boshlash
    @PostMapping("/quizzes/{quizId}/start")
    public ResponseEntity<Attempt> startAttempt(@PathVariable Integer quizId,
                                                @RequestParam Integer userId) {
        return ResponseEntity.ok(attemptService.startAttempt(quizId, userId));
    }

    // Javob yuborish
    @PostMapping("/questions/{questionId}/answer")
    public ResponseEntity<Answer> submitAnswer(@PathVariable Integer questionId,
                                               @RequestParam Integer userId,
                                               @RequestParam String selectedOption) {
        return ResponseEntity.ok(answerService.submitAnswer(questionId, userId, selectedOption));
    }

    // Quizni tugatish
    @PutMapping("/attempts/{attemptId}/finish")
    public ResponseEntity<Attempt> finishAttempt(@PathVariable Integer attemptId) {
        return ResponseEntity.ok(attemptService.finishAttempt(attemptId));
    }

    // Natijani ko‘rish
    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<Result> getResult(@PathVariable Integer attemptId) {
        return ResponseEntity.ok(resultService.calculateResult(attemptId));
    }
}
