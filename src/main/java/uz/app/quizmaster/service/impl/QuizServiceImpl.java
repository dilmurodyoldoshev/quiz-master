package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.dto.QuizDto;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.helper.Helper;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.service.QuizService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<Quiz> createQuiz(QuizDto quizDto) {
        try {
            User teacher = Helper.getCurrentPrincipal();

            if (quizDto.getDurationMinutes() == null || quizDto.getDurationMinutes() <= 0) {
                return ResponseMessage.fail("Duration must be a positive number (minutes)", null);
            }

            Quiz quiz = new Quiz();
            quiz.setTitle(quizDto.getTitle());
            quiz.setDescription(quizDto.getDescription());
            quiz.setCheatingControl(Boolean.TRUE.equals(quizDto.getCheatingControl()));
            quiz.setDurationMinutes(quizDto.getDurationMinutes());
            quiz.setCreatedBy(teacher);
            quiz.setIsActive(false);

            Quiz saved = quizRepository.save(quiz);
            return ResponseMessage.success("Quiz created successfully", saved);
        } catch (Exception e) {
            log.error("Error creating quiz: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error creating quiz", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<Quiz> activateQuiz(Integer quizId) {
        try {
            User teacher = Helper.getCurrentPrincipal();

            return quizRepository.findById(quizId)
                    .map(quiz -> {
                        if (!quiz.getCreatedBy().equals(teacher)) {
                            return ResponseMessage.<Quiz>fail("You are not allowed to activate this quiz", null);
                        }
                        if (quiz.getIsActive()) {
                            return ResponseMessage.<Quiz>fail("Quiz is already active", quiz);
                        }
                        if (quiz.getDurationMinutes() == null || quiz.getDurationMinutes() <= 0) {
                            return ResponseMessage.<Quiz>fail("Quiz duration is not set", null);
                        }
                        quiz.setIsActive(true);
                        quizRepository.save(quiz);
                        return ResponseMessage.success("Quiz activated successfully", quiz);
                    })
                    .orElse(ResponseMessage.fail("Quiz not found", null));
        } catch (Exception e) {
            log.error("Error activating quiz: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error activating quiz", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<Quiz> finishQuiz(Integer quizId) {
        try {
            User teacher = Helper.getCurrentPrincipal();

            return quizRepository.findById(quizId)
                    .map(quiz -> {
                        if (!quiz.getCreatedBy().equals(teacher)) {
                            return ResponseMessage.<Quiz>fail("You are not allowed to finish this quiz", null);
                        }
                        if (!quiz.getIsActive()) {
                            return ResponseMessage.<Quiz>fail("Quiz is already finished", quiz);
                        }
                        quiz.setIsActive(false);
                        quizRepository.save(quiz);
                        return ResponseMessage.success("Quiz finished successfully", quiz);
                    })
                    .orElse(ResponseMessage.fail("Quiz not found", null));
        } catch (Exception e) {
            log.error("Error finishing quiz: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error finishing quiz", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<Quiz> toggleCheatingControl(Integer quizId, Boolean enabled) {
        try {
            User teacher = Helper.getCurrentPrincipal();

            return quizRepository.findById(quizId)
                    .map(quiz -> {
                        if (!quiz.getCreatedBy().equals(teacher)) {
                            return ResponseMessage.<Quiz>fail("You are not allowed to change this quiz", null);
                        }
                        quiz.setCheatingControl(Boolean.TRUE.equals(enabled));
                        quizRepository.save(quiz);
                        return ResponseMessage.success("Cheating control set to: " + enabled, quiz);
                    })
                    .orElse(ResponseMessage.fail("Quiz not found", null));
        } catch (Exception e) {
            log.error("Error updating cheating control: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error updating cheating control", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<List<Quiz>> getAllQuizzes() {
        try {
            User teacher = Helper.getCurrentPrincipal();
            List<Quiz> quizzes = quizRepository.findByCreatedBy(teacher);
            return ResponseMessage.success("Your quizzes fetched successfully", quizzes);
        } catch (Exception e) {
            log.error("Error fetching quizzes: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error fetching quizzes", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<Quiz> getQuizById(Integer quizId) {
        try {
            User teacher = Helper.getCurrentPrincipal();

            return quizRepository.findById(quizId)
                    .map(quiz -> {
                        if (!quiz.getCreatedBy().equals(teacher)) {
                            return ResponseMessage.<Quiz>fail("You are not allowed to view this quiz", null);
                        }
                        return ResponseMessage.success("Quiz fetched successfully", quiz);
                    })
                    .orElse(ResponseMessage.fail("Quiz not found", null));
        } catch (Exception e) {
            log.error("Error fetching quiz: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error fetching quiz", null);
        }
    }

    @Override
    public ResponseMessage<List<QuizDto>> getAllQuizzesPublic() {
        try {
            List<QuizDto> quizzes = quizRepository.findByIsActiveTrue()
                    .stream()
                    .map(q -> {
                        QuizDto dto = new QuizDto();
                        dto.setId(q.getId()); // 🔑 id qo‘shildi
                        dto.setTitle(q.getTitle());
                        dto.setDescription(q.getDescription());
                        dto.setCheatingControl(q.getCheatingControl());
                        dto.setDurationMinutes(q.getDurationMinutes());
                        return dto;
                    })
                    .toList();

            return ResponseMessage.success("All active quizzes fetched successfully", quizzes);
        } catch (Exception e) {
            log.error("Error fetching public quizzes: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error fetching public quizzes", null);
        }
    }

    @Override
    public ResponseMessage<QuizDto> getQuizByIdPublic(Integer quizId) {
        try {
            return quizRepository.findByIdAndIsActiveTrue(quizId)
                    .map(q -> {
                        QuizDto dto = new QuizDto();
                        dto.setId(q.getId()); // 🔑 id qo‘shildi
                        dto.setTitle(q.getTitle());
                        dto.setDescription(q.getDescription());
                        dto.setCheatingControl(q.getCheatingControl());
                        dto.setDurationMinutes(q.getDurationMinutes());
                        return ResponseMessage.success("Quiz fetched successfully", dto);
                    })
                    .orElse(ResponseMessage.fail("Quiz not found or not active", null));
        } catch (Exception e) {
            log.error("Error fetching public quiz with id {}: {}", quizId, e.getMessage(), e);
            return ResponseMessage.fail("Error fetching public quiz", null);
        }
    }
}
