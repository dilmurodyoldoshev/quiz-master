package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.app.quizmaster.dto.*;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.service.*;

import java.util.List;

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
    public ResponseMessage<List<QuizDto>> getAllQuizzes() {
        return quizService.getAllQuizzesPublic();
    }

    @GetMapping("/quizzes/{quizId}")
    public ResponseMessage<QuizDto> getQuiz(@PathVariable Integer quizId) {
        return quizService.getQuizByIdPublic(quizId);
    }

    // 2. Savollar
    @GetMapping("/quizzes/{quizId}/questions")
    public ResponseMessage<List<QuestionDto>> getAllQuestions(@PathVariable Integer quizId) {
        return questionService.getAllQuestionsPublic(quizId);
    }

    @GetMapping("/quizzes/{quizId}/questions/{questionId}")
    public ResponseMessage<QuestionDto> getQuestion(
            @PathVariable Integer quizId,
            @PathVariable Integer questionId) {
        return questionService.getQuestionPublic(quizId, questionId);
    }

    // 3. Attempt
    @PostMapping("/quizzes/{quizId}/start")
    public ResponseMessage<AttemptDto> startAttempt(@PathVariable Integer quizId) {
        return attemptService.startAttempt(quizId);
    }

    @PostMapping("/attempts/{attemptId}/finish")
    public ResponseMessage<AttemptDto> finishAttempt(@PathVariable Integer attemptId) {
        return attemptService.finishAttempt(attemptId);
    }

    // 4. Javob yuborish
    @PostMapping("/attempts/{attemptId}/questions/{questionId}/answer")
    public ResponseMessage<AnswerDto> submitAnswer(
            @PathVariable Integer attemptId,
            @PathVariable Integer questionId,
            @RequestParam String selectedOption) {
        return answerService.submitAnswer(attemptId, questionId, selectedOption);
    }

    // 5. Natijalar va leaderboard
    @GetMapping("/quizzes/{quizId}/leaderboard")
    public ResponseMessage<List<LeaderboardEntryDto>> getLeaderboard(@PathVariable Integer quizId) {
        return resultService.getLeaderboard(quizId);
    }

    // result
    @GetMapping("/results")
    public ResponseMessage<List<ResultDto>> getMyResults() {
        return resultService.getResultsForCurrentUser();
    }

}
