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
@RequiredArgsConstructor
public class TeacherController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final ResultService resultService;

    // Teacherning barcha quizlarini olish
    @GetMapping("/quizzes")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> getAllQuizzes() {
        return buildResponse(quizService.getAllQuizzes());
    }

    // Teacherning quizId bo‘yicha bitta quizini olish
    @GetMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> getQuizById(@PathVariable Integer quizId) {
        return buildResponse(quizService.getQuizById(quizId));
    }

    // Quiz yaratish
    @PostMapping("/quizzes/create")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> createQuiz(@RequestBody QuizDto quizDto) {
        return buildResponse(quizService.createQuiz(quizDto));
    }

    // Quizni aktiv qilish
    @PutMapping("/quizzes/{quizId}/activate")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> activateQuiz(@PathVariable Integer quizId) {
        return buildResponse(quizService.activateQuiz(quizId));
    }

    // Quizni tugatish
    @PutMapping("/quizzes/{quizId}/finish")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> finishQuiz(@PathVariable Integer quizId) {
        return buildResponse(quizService.finishQuiz(quizId));
    }

    // Cheating control qo‘shish
    @PutMapping("/quizzes/{quizId}/cheating")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> toggleCheating(
            @PathVariable Integer quizId,
            @RequestParam Boolean enabled
    ) {
        return buildResponse(quizService.toggleCheatingControl(quizId, enabled));
    }

    // Teacherning quizidagi barcha savollarni olish
    @GetMapping("/quizzes/{quizId}/questions")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> getAllQuestions(@PathVariable Integer quizId) {
        return buildResponse(questionService.getAllQuestions(quizId));
    }

    // Teacherning quizidagi bitta savolni olish
    @GetMapping("/quizzes/{quizId}/questions/{questionId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> getQuestion(
            @PathVariable Integer quizId,
            @PathVariable Integer questionId
    ) {
        return buildResponse(questionService.getQuestion(quizId, questionId));
    }

    // Savol qo‘shish
    @PostMapping("/quizzes/{quizId}/questions/create")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> addQuestion(
            @PathVariable Integer quizId,
            @RequestBody QuestionDto dto
    ) {
        return buildResponse(questionService.addQuestion(quizId, dto));
    }

    // Savolni yangilash
    @PutMapping("/quizzes/{quizId}/questions/{questionId}/update")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> updateQuestion(
            @PathVariable Integer quizId,
            @PathVariable Integer questionId,
            @RequestBody QuestionDto dto
    ) {
        return buildResponse(questionService.updateQuestion(quizId, questionId, dto));
    }

    // 🔹 Leaderboard olish
    @GetMapping("/quizzes/{quizId}/results")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> getLeaderboard(@PathVariable Integer quizId) {
        return buildResponse(resultService.getLeaderboard(quizId));
    }
}
