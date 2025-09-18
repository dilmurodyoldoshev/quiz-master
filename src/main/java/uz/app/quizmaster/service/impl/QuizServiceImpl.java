package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.dto.QuizDto;
import uz.app.quizmaster.dto.UserDto;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.helper.Helper;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.service.QuizService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    // ✅ DTO mapping helper
    private QuizDto mapToDto(Quiz quiz) {
        QuizDto dto = new QuizDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setCheatingControl(quiz.getCheatingControl());
        dto.setDurationMinutes(quiz.getDurationMinutes());

        User createdBy = quiz.getCreatedBy();
        if (createdBy != null) {
            UserDto userDto = new UserDto();
            userDto.setFirstName(createdBy.getFirstName());
            userDto.setLastName(createdBy.getLastName());
            userDto.setUsername(createdBy.getUsername());
            userDto.setPhone(createdBy.getPhone());
            userDto.setEmail(createdBy.getEmail());
            userDto.setRole(createdBy.getRole());
            dto.setCreateBy(userDto);
        }
        return dto;
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<QuizDto> createQuiz(QuizDto quizDto) {
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
            return ResponseMessage.success("Quiz created successfully", mapToDto(saved));
        } catch (Exception e) {
            log.error("Error creating quiz: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error creating quiz", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<QuizDto> activateQuiz(Integer quizId) {
        try {
            User teacher = Helper.getCurrentPrincipal();

            return quizRepository.findById(quizId)
                    .map(quiz -> {
                        if (!Objects.equals(quiz.getCreatedBy().getId(), teacher.getId())) {
                            return ResponseMessage.<QuizDto>fail("You are not allowed to activate this quiz", null);
                        }
                        if (quiz.getIsActive()) {
                            return ResponseMessage.<QuizDto>fail("Quiz is already active", mapToDto(quiz));
                        }
                        if (quiz.getDurationMinutes() == null || quiz.getDurationMinutes() <= 0) {
                            return ResponseMessage.<QuizDto>fail("Quiz duration is not set", null);
                        }
                        quiz.setIsActive(true);
                        quizRepository.save(quiz);
                        return ResponseMessage.success("Quiz activated successfully", mapToDto(quiz));
                    })
                    .orElse(ResponseMessage.fail("Quiz not found", null));
        } catch (Exception e) {
            log.error("Error activating quiz: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error activating quiz", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<QuizDto> finishQuiz(Integer quizId) {
        try {
            User teacher = Helper.getCurrentPrincipal();

            return quizRepository.findById(quizId)
                    .map(quiz -> {
                        if (!Objects.equals(quiz.getCreatedBy().getId(), teacher.getId())) {
                            return ResponseMessage.<QuizDto>fail("You are not allowed to finish this quiz", null);
                        }
                        if (!quiz.getIsActive()) {
                            return ResponseMessage.<QuizDto>fail("Quiz is already finished", mapToDto(quiz));
                        }
                        quiz.setIsActive(false);
                        quizRepository.save(quiz);
                        return ResponseMessage.success("Quiz finished successfully", mapToDto(quiz));
                    })
                    .orElse(ResponseMessage.fail("Quiz not found", null));
        } catch (Exception e) {
            log.error("Error finishing quiz: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error finishing quiz", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<QuizDto> toggleCheatingControl(Integer quizId, Boolean enabled) {
        try {
            User teacher = Helper.getCurrentPrincipal();

            return quizRepository.findById(quizId)
                    .map(quiz -> {
                        if (!Objects.equals(quiz.getCreatedBy().getId(), teacher.getId())) {
                            return ResponseMessage.<QuizDto>fail("You are not allowed to change this quiz", null);
                        }
                        quiz.setCheatingControl(Boolean.TRUE.equals(enabled));
                        quizRepository.save(quiz);
                        return ResponseMessage.success("Cheating control set to: " + enabled, mapToDto(quiz));
                    })
                    .orElse(ResponseMessage.fail("Quiz not found", null));
        } catch (Exception e) {
            log.error("Error updating cheating control: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error updating cheating control", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<List<QuizDto>> getAllQuizzes() {
        try {
            User teacher = Helper.getCurrentPrincipal();
            List<Quiz> quizzes = quizRepository.findByCreatedBy(teacher);
            List<QuizDto> quizDtos = quizzes.stream().map(this::mapToDto).toList();
            return ResponseMessage.success("Your quizzes fetched successfully", quizDtos);
        } catch (Exception e) {
            log.error("Error fetching quizzes: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error fetching quizzes", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<QuizDto> getQuizById(Integer quizId) {
        try {
            User teacher = Helper.getCurrentPrincipal();

            return quizRepository.findById(quizId)
                    .map(quiz -> {
                        if (!Objects.equals(quiz.getCreatedBy().getId(), teacher.getId())) {
                            return ResponseMessage.<QuizDto>fail("You are not allowed to view this quiz", null);
                        }
                        return ResponseMessage.success("Quiz fetched successfully", mapToDto(quiz));
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
                    .map(this::mapToDto)
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
                    .map(q -> ResponseMessage.success("Quiz fetched successfully", mapToDto(q)))
                    .orElse(ResponseMessage.fail("Quiz not found or not active", null));
        } catch (Exception e) {
            log.error("Error fetching public quiz with id {}: {}", quizId, e.getMessage(), e);
            return ResponseMessage.fail("Error fetching public quiz", null);
        }
    }
}
