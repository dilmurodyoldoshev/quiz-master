package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.quizmaster.dto.*;
import uz.app.quizmaster.helper.Helper;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.service.*;

import java.util.List;
import java.util.Map;

import static uz.app.quizmaster.helper.Helper.buildResponse;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final AttemptService attemptService;
    private final AnswerService answerService;
    private final ResultService resultService;

    // 1. Quizlar
    @GetMapping("/quizzes")
    public ResponseEntity<ResponseMessage> getAllQuizzes() {
        return buildResponse(quizService.getAllQuizzesPublic());
    }

    @GetMapping("/quizzes/{quizId}")
    public ResponseEntity<ResponseMessage> getQuiz(@PathVariable Integer quizId) {
        return buildResponse(quizService.getQuizByIdPublic(quizId));
    }

    // 2. Savollar
    @GetMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<ResponseMessage> getAllQuestions(@PathVariable Integer quizId) {
        return buildResponse(questionService.getAllQuestionsPublic(quizId));
    }

    @GetMapping("/quizzes/{quizId}/questions/{questionId}")
    public ResponseEntity<ResponseMessage> getQuestion(
            @PathVariable Integer quizId,
            @PathVariable Integer questionId) {
        return buildResponse(questionService.getQuestionPublic(quizId, questionId));
    }

    // 3. Attempt
    @PostMapping("/quizzes/{quizId}/start")
    public ResponseEntity<ResponseMessage> startAttempt(@PathVariable Integer quizId) {
        return buildResponse(attemptService.startAttempt(quizId));
    }

    @PostMapping("/attempts/{attemptId}/finish")
    public ResponseEntity<ResponseMessage> finishAttempt(
            @PathVariable Integer attemptId,
            @RequestBody Map<Integer, String> answers) {
        ResponseMessage response = attemptService.finishAttempt(attemptId, answers);
        return Helper.buildResponse(response);
    }

    // 6. Student urinishlari
    @GetMapping("/attempts")
    public ResponseEntity<ResponseMessage> getMyAttempts() {
        return buildResponse(attemptService.getAttempts());
    }

    // 4. Javob yuborish
    @PostMapping("/attempts/{attemptId}/questions/{questionId}/answer")
    public ResponseEntity<ResponseMessage> submitAnswer(
            @PathVariable Integer attemptId,
            @PathVariable Integer questionId,
            @RequestBody AnswerRequestDto requestDto) {
        return buildResponse(answerService.submitAnswer(attemptId, questionId, requestDto.getSelectedOption()));
    }

    // 5. Natijalar va leaderboard
    @GetMapping("/quizzes/{quizId}/leaderboard")
    public ResponseEntity<ResponseMessage> getLeaderboard(@PathVariable Integer quizId) {
        return buildResponse(resultService.getLeaderboardForQuiz(quizId));
    }

    // Studentning o‘z resultlari
    @GetMapping("/results")
    public ResponseEntity<ResponseMessage> getMyResults() {
        return buildResponse(resultService.getMyResults());
    }
}
