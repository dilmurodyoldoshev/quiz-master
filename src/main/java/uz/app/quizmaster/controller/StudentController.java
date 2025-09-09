package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.service.AnswerService;
import uz.app.quizmaster.service.AttemptService;
import uz.app.quizmaster.service.ResultService;

import static uz.app.quizmaster.helper.Helper.buildResponse;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final AttemptService attemptService;
    private final AnswerService answerService;
    private final ResultService resultService;

    // 🔹 Quizni boshlash
    @PostMapping("/quizzes/{quizId}/start")
    public ResponseEntity<ResponseMessage> startAttempt(@PathVariable Integer quizId) {
        return buildResponse(attemptService.startAttempt(quizId));
    }

    // 🔹 Javob yuborish
    @PostMapping("/questions/{questionId}/answer")
    public ResponseEntity<ResponseMessage> submitAnswer(@PathVariable Integer questionId,
                                                        @RequestParam String selectedOption) {
        return buildResponse(answerService.submitAnswer(questionId, selectedOption));
    }

    // 🔹 Quizni tugatish
    @PutMapping("/attempts/{attemptId}/finish")
    public ResponseEntity<ResponseMessage> finishAttempt(@PathVariable Integer attemptId) {
        return buildResponse(attemptService.finishAttempt(attemptId));
    }

    // 🔹 Natijani ko‘rish
    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<ResponseMessage> getResult(@PathVariable Integer attemptId) {
        return buildResponse(resultService.calculateResult(attemptId));
    }
}
