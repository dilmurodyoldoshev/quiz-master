package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.entity.Answer;
import uz.app.quizmaster.entity.Attempt;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.repository.AnswerRepository;
import uz.app.quizmaster.repository.AttemptRepository;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.repository.UserRepository;
import uz.app.quizmaster.service.AttemptService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepository attemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    @Override
    public Attempt startAttempt(Integer quizId, Integer userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        if (!quiz.getIsActive()) {
            throw new IllegalStateException("Quiz is not active!");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Attempt attempt = new Attempt();
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setCheatingDetected(false);
        attempt.setScore(0);

        return attemptRepository.save(attempt);
    }

    @Override
    public Attempt finishAttempt(Integer attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new NoSuchElementException("Attempt not found"));

        attempt.setFinishedAt(LocalDateTime.now());

        // Studentning barcha javoblarini olib kelamiz
        List<Answer> answers = answerRepository.findAll()
                .stream()
                .filter(a -> a.getUser().getId().equals(attempt.getUser().getId()))
                .filter(a -> a.getQuestion().getQuiz().getId().equals(attempt.getQuiz().getId()))
                .toList();

        // To‘g‘ri javoblar sonini hisoblaymiz
        int score = (int) answers.stream()
                .filter(Answer::getIsCorrect)
                .count();

        attempt.setScore(score);

        return attemptRepository.save(attempt);
    }
}
