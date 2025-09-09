package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.app.quizmaster.entity.Answer;
import uz.app.quizmaster.entity.Attempt;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.helper.Helper;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.AnswerRepository;
import uz.app.quizmaster.repository.AttemptRepository;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.service.AttemptService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepository attemptRepository;
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseMessage startAttempt(Integer quizId) {
        User student = Helper.getCurrentPrincipal();

        Optional<Quiz> optQuiz = quizRepository.findById(quizId);
        if (optQuiz.isEmpty()) {
            return new ResponseMessage(false, "Quiz not found", null);
        }

        Quiz quiz = optQuiz.get();

        if (!Boolean.TRUE.equals(quiz.getIsActive())) {
            return new ResponseMessage(false, "Quiz is not active", null);
        }

        LocalDateTime now = LocalDateTime.now();
        if (quiz.getStartTime() != null && now.isBefore(quiz.getStartTime())) {
            return new ResponseMessage(false, "Quiz has not started yet", null);
        }
        if (quiz.getEndTime() != null && now.isAfter(quiz.getEndTime())) {
            return new ResponseMessage(false, "Quiz has already finished", null);
        }

        // ❗️Bitta userga bitta urinish (attempt). Agar ko‘p urinish bo‘lsin desang, bu cheklovni o‘zgartirasan.
        Optional<Attempt> existing = attemptRepository.findFirstByUserIdAndQuizIdOrderByStartedAtDesc(
                student.getId(), quizId
        );
        if (existing.isPresent()) {
            Attempt a = existing.get();
            if (a.getFinishedAt() == null) {
                return new ResponseMessage(false, "You already have an active attempt for this quiz", a);
            }
            return new ResponseMessage(false, "You have already attempted this quiz", a);
        }

        Attempt attempt = new Attempt();
        attempt.setQuiz(quiz);
        attempt.setUser(student);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setFinishedAt(null);
        attempt.setCheatingDetected(false);
        attempt.setScore(0);

        Attempt saved = attemptRepository.save(attempt);
        return new ResponseMessage(true, "Attempt started", saved);
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public ResponseMessage finishAttempt(Integer attemptId) {
        User student = Helper.getCurrentPrincipal();

        Optional<Attempt> optAttempt = attemptRepository.findById(attemptId);
        if (optAttempt.isEmpty()) {
            return new ResponseMessage(false, "Attempt not found", null);
        }

        Attempt attempt = optAttempt.get();

        // Faqat o‘z attemptini tugatishi mumkin
        if (!attempt.getUser().getId().equals(student.getId())) {
            return new ResponseMessage(false, "You are not allowed to finish this attempt", null);
        }

        if (attempt.getFinishedAt() != null) {
            return new ResponseMessage(false, "Attempt is already finished", attempt);
        }

        // Tugatish va score hisoblash
        attempt.setFinishedAt(LocalDateTime.now());

        List<Answer> answers = answerRepository.findByUserIdAndQuestionQuizId(student.getId(), attempt.getQuiz().getId());
        int score = (int) answers.stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).count();
        attempt.setScore(score);

        Attempt saved = attemptRepository.save(attempt);
        return new ResponseMessage(true, "Attempt finished", saved);
    }
}
