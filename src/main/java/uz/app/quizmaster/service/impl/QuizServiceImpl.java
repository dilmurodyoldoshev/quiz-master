package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.dto.QuizDto;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.helper.Helper;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.service.QuizService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage createQuiz(QuizDto quizDto) {
        User teacher = Helper.getCurrentPrincipal(); // current teacher

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDto.getTitle());
        quiz.setDescription(quizDto.getDescription());
        quiz.setCheatingControl(quizDto.getCheatingControl());
        quiz.setStartTime(quizDto.getStartTime());
        quiz.setEndTime(quizDto.getEndTime());
        quiz.setCreatedBy(teacher);
        quiz.setIsActive(false);

        Quiz savedQuiz = quizRepository.save(quiz);

        return new ResponseMessage(
                true,
                "Quiz created successfully",
                savedQuiz
        );
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage activateQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        if (quiz.getIsActive()) {
            return new ResponseMessage(false, "Quiz is already active", quiz);
        }

        quiz.setIsActive(true);
        if (quiz.getStartTime() == null) {
            quiz.setStartTime(LocalDateTime.now());
        }

        quizRepository.save(quiz);

        return new ResponseMessage(true, "Quiz activated successfully", quiz);
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage finishQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        if (!quiz.getIsActive()) {
            return new ResponseMessage(false, "Quiz is already finished", quiz);
        }

        quiz.setIsActive(false);
        quiz.setEndTime(LocalDateTime.now());

        quizRepository.save(quiz);

        return new ResponseMessage(true, "Quiz finished successfully", quiz);
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage toggleCheatingControl(Integer quizId, Boolean enabled) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        quiz.setCheatingControl(enabled);

        quizRepository.save(quiz);

        return new ResponseMessage(
                true,
                "Cheating control is now set to: " + enabled,
                quiz
        );
    }

    @Override
    public ResponseMessage getAllQuizzes() {
        User teacher = Helper.getCurrentPrincipal();

        List<Quiz> quizzes = quizRepository.findByCreatedBy(teacher);

        return new ResponseMessage(
                true,
                "Your quizzes fetched successfully",
                quizzes
        );
    }

    @Override
    public ResponseMessage getQuizById(Integer quizId) {
        User teacher = Helper.getCurrentPrincipal();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        if (!quiz.getCreatedBy().equals(teacher)) {
            return new ResponseMessage(false, "You are not allowed to view this quiz", null);
        }

        return new ResponseMessage(
                true,
                "Quiz fetched successfully",
                quiz
        );
    }

    @Override
    public ResponseMessage getAllQuizzesPublic() {
        // Faqat active bo‘lgan quizlarni ko‘rsatish mumkin
        List<Quiz> quizzes = quizRepository.findAll()
                .stream()
                .filter(Quiz::getIsActive) // faqat aktiv quizlar
                .toList();

        return new ResponseMessage(
                true,
                "All active quizzes fetched successfully",
                quizzes
        );
    }

    @Override
    public ResponseMessage getQuizByIdPublic(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        if (!quiz.getIsActive()) {
            return new ResponseMessage(false, "This quiz is not active", null);
        }

        return new ResponseMessage(
                true,
                "Quiz fetched successfully",
                quiz
        );
    }

}
