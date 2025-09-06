package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.repository.UserRepository;
import uz.app.quizmaster.service.QuizService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Override
    public Quiz createQuiz(Quiz quiz, Integer teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new NoSuchElementException("Teacher not found"));

        quiz.setCreatedBy(teacher);
        quiz.setIsActive(false);
        quiz.setCheatingControl(false);
        quiz.setStartTime(null);
        quiz.setEndTime(null);

        return quizRepository.save(quiz);
    }

    @Override
    public Quiz activateQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));
        quiz.setIsActive(true);
        quiz.setStartTime(LocalDateTime.now());

        return quizRepository.save(quiz);
    }

    @Override
    public Quiz finishQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));
        quiz.setIsActive(false);
        quiz.setEndTime(LocalDateTime.now());

        return quizRepository.save(quiz);
    }

    @Override
    public Quiz toggleCheatingControl(Integer quizId, Boolean enabled) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));
        quiz.setCheatingControl(enabled);

        return quizRepository.save(quiz);
    }

    @Override
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Quiz::getStartTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList(); // Java 16+ da mavjud
    }

    @Override
    public Quiz getQuizById(Integer id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));
    }
}
