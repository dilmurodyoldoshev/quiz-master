package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.app.quizmaster.dto.QuestionDto;
import uz.app.quizmaster.dto.QuizDto;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.service.QuestionService;
import uz.app.quizmaster.service.QuizService;
import uz.app.quizmaster.service.ResultService;

import static uz.app.quizmaster.helper.Helper.buildResponse;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasRole('TEACHER')")
@RequiredArgsConstructor
public class TeacherController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final ResultService resultService;

    // ------------------- Quiz Endpoints -------------------

    @GetMapping("/quizzes")
    public ResponseEntity<ResponseMessage> getAllQuizzes() {
        return buildResponse(quizService.getAllQuizzes());
    }

    @GetMapping("/quizzes/{quizId}")
    public ResponseEntity<ResponseMessage> getQuizById(@PathVariable Integer quizId) {
        return buildResponse(quizService.getQuizById(quizId));
    }

    @PostMapping("/quizzes")
    public ResponseEntity<ResponseMessage> createQuiz(@RequestBody QuizDto quizDto) {
        return buildResponse(quizService.createQuiz(quizDto));
    }

    @PutMapping("/quizzes/{quizId}/activate")
    public ResponseEntity<ResponseMessage> activateQuiz(@PathVariable Integer quizId) {
        return buildResponse(quizService.activateQuiz(quizId));
    }

    @PutMapping("/quizzes/{quizId}/finish")
    public ResponseEntity<ResponseMessage> finishQuiz(@PathVariable Integer quizId) {
        return buildResponse(quizService.finishQuiz(quizId)); // service’da finishQuiz bo‘lishi kerak
    }

    @PutMapping("/quizzes/{quizId}/cheating")
    public ResponseEntity<ResponseMessage> toggleCheating(
            @PathVariable Integer quizId,
            @RequestParam Boolean enabled
    ) {
        return buildResponse(quizService.toggleCheatingControl(quizId, enabled));
    }

    // ------------------- Question Endpoints -------------------

    @GetMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<ResponseMessage> getAllQuestions(@PathVariable Integer quizId) {
        return buildResponse(questionService.getAllQuestions(quizId));
    }

    @GetMapping("/quizzes/{quizId}/questions/{questionId}")
    public ResponseEntity<ResponseMessage> getQuestion(
            @PathVariable Integer quizId,
            @PathVariable Integer questionId
    ) {
        return buildResponse(questionService.getQuestion(quizId, questionId));
    }

    @PostMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<ResponseMessage> addQuestion(
            @PathVariable Integer quizId,
            @RequestBody QuestionDto dto
    ) {
        return buildResponse(questionService.addQuestion(quizId, dto));
    }

    @PutMapping("/quizzes/{quizId}/questions/{questionId}")
    public ResponseEntity<ResponseMessage> updateQuestion(
            @PathVariable Integer quizId,
            @PathVariable Integer questionId,
            @RequestBody QuestionDto dto
    ) {
        return buildResponse(questionService.updateQuestion(quizId, questionId, dto));
    }

    // ------------------- Leaderboard/Result -------------------

    @GetMapping("/quizzes/{quizId}/leaderboard")
    public ResponseEntity<ResponseMessage> getLeaderboard(@PathVariable Integer quizId) {
        return buildResponse(resultService.getResultsForMyQuizzes(quizId));
    }
}
