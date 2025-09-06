package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.quizmaster.entity.Question;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.Result;
import uz.app.quizmaster.service.QuestionService;
import uz.app.quizmaster.service.QuizService;
import uz.app.quizmaster.service.ResultService;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final ResultService resultService;

    // Quiz yaratish
    @PostMapping("/quizzes")
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz,
                                           @RequestParam Integer teacherId) {
        return ResponseEntity.ok(quizService.createQuiz(quiz, teacherId));
    }

    // Quizni aktiv qilish
    @PutMapping("/quizzes/{quizId}/activate")
    public ResponseEntity<Quiz> activateQuiz(@PathVariable Integer quizId) {
        return ResponseEntity.ok(quizService.activateQuiz(quizId));
    }

    // Quizni tugatish
    @PutMapping("/quizzes/{quizId}/finish")
    public ResponseEntity<Quiz> finishQuiz(@PathVariable Integer quizId) {
        return ResponseEntity.ok(quizService.finishQuiz(quizId));
    }

    // Cheating control qo‘shish
    @PutMapping("/quizzes/{quizId}/cheating")
    public ResponseEntity<Quiz> toggleCheating(@PathVariable Integer quizId,
                                               @RequestParam Boolean enabled) {
        return ResponseEntity.ok(quizService.toggleCheatingControl(quizId, enabled));
    }

    // Savol qo‘shish
    @PostMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<Question> addQuestion(@PathVariable Integer quizId,
                                                @RequestBody Question question,
                                                @RequestParam Integer teacherId) {
        return ResponseEntity.ok(questionService.addQuestion(quizId, question, teacherId));
    }

    // Savollarni olish
    @GetMapping("/quizzes/{quizId}/questions")
    public ResponseEntity<List<Question>> getQuestions(@PathVariable Integer quizId) {
        return ResponseEntity.ok(questionService.getQuestionsByQuiz(quizId));
    }

    // Natijalarni olish (leaderboard)
    @GetMapping("/quizzes/{quizId}/results")
    public ResponseEntity<List<Result>> getResults(@PathVariable Integer quizId) {
        return ResponseEntity.ok(resultService.getLeaderboard(quizId));
    }
}
