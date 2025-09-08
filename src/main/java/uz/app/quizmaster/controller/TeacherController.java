package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.app.quizmaster.dto.QuestionDto;
import uz.app.quizmaster.dto.QuizDto;
import uz.app.quizmaster.entity.Result;
import uz.app.quizmaster.payload.ResponseMessage;
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

    // Teacherning barcha quizlarini olish
    @GetMapping("/quizzes")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> getAllQuizzes() {
        ResponseMessage response = quizService.getAllQuizzes(); // faqat o'z teacherining quizlari
        return ResponseEntity.ok(response);
    }

    // Teacherning quizId bo‘yicha bitta quizini olish
    @GetMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> getQuizById(@PathVariable Integer quizId) {
        ResponseMessage response = quizService.getQuizById(quizId); // faqat shu teacherning quizlari
        return ResponseEntity.ok(response);
    }

    // Quiz yaratish
    @PostMapping("/quizzes/creat")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> createQuiz(
            @RequestBody QuizDto quizDto
    ) {
        ResponseMessage response = quizService.createQuiz(quizDto);
        return ResponseEntity.ok(response);
    }


    // Quizni aktiv qilish
    @PutMapping("/quizzes/{quizId}/activate")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> activateQuiz(@PathVariable Integer quizId) {
        ResponseMessage response = quizService.activateQuiz(quizId);
        return ResponseEntity.ok(response);
    }

    // Quizni tugatish
    @PutMapping("/quizzes/{quizId}/finish")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> finishQuiz(@PathVariable Integer quizId) {
        ResponseMessage response = quizService.finishQuiz(quizId);
        return ResponseEntity.ok(response);
    }

    // Cheating control qo‘shish
    @PutMapping("/quizzes/{quizId}/cheating")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> toggleCheating(@PathVariable Integer quizId,
                                                          @RequestParam Boolean enabled) {
        ResponseMessage response = quizService.toggleCheatingControl(quizId, enabled);
        return ResponseEntity.ok(response);
    }

    // Teacherning quizidagi barcha savollarni olish
    @GetMapping("/quizzes/{quizId}/questions")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> getAllQuestions(@PathVariable Integer quizId) {
        ResponseMessage response = questionService.getAllQuestions(quizId); // faqat shu teacherning quizlari
        return ResponseEntity.ok(response);
    }

    // Teacherning quizidagi bitta savolni olish
    @GetMapping("/quizzes/{quizId}/questions/{questionId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> getQuestion(
            @PathVariable Integer quizId,
            @PathVariable Integer questionId
    ) {
        ResponseMessage response = questionService.getQuestion(quizId, questionId); // faqat shu teacherning quizlari
        return ResponseEntity.ok(response);
    }

    // Savol qo‘shish
    @PostMapping("/quizzes/{quizId}/questions/create")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> addQuestion(
            @PathVariable Integer quizId,
            @RequestBody QuestionDto dto
    ) {
        ResponseMessage response = questionService.addQuestion(quizId, dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/quizzes/{quizId}/questions/{questionId}/update")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ResponseMessage> updateQuestion(
            @PathVariable Integer quizId,
            @PathVariable Integer questionId,
            @RequestBody QuestionDto dto
    ) {
        ResponseMessage response = questionService.updateQuestion(quizId, questionId, dto);
        return ResponseEntity.ok(response);
    }

    // Natijalarni olish (leaderboard)
    @GetMapping("/quizzes/{quizId}/results")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<Result>> getResults(@PathVariable Integer quizId) {
        return ResponseEntity.ok(resultService.getLeaderboard(quizId));
    }
}
