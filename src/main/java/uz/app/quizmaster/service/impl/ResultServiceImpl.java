package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.entity.Attempt;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.Result;
import uz.app.quizmaster.repository.AttemptRepository;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.repository.ResultRepository;
import uz.app.quizmaster.service.ResultService;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final AttemptRepository attemptRepository;
    private final QuizRepository quizRepository;

    @Override
    public Result calculateResult(Integer attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new NoSuchElementException("Attempt not found"));

        Quiz quiz = attempt.getQuiz();

        // Agar natija allaqachon mavjud bo‘lsa qaytaramiz
        Result existing = resultRepository.findAll()
                .stream()
                .filter(r -> r.getQuiz().getId().equals(quiz.getId()))
                .filter(r -> r.getUser().getId().equals(attempt.getUser().getId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return existing;
        }

        Result result = new Result();
        result.setQuiz(quiz);
        result.setUser(attempt.getUser());
        result.setScore(attempt.getScore());
        result.setCompletedAt(LocalDateTime.now());

        return resultRepository.save(result);
    }

    @Override
    public List<Result> getLeaderboard(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        // Shu quiz bo‘yicha barcha resultlarni olamiz
        List<Result> results = resultRepository.findAll()
                .stream()
                .filter(r -> r.getQuiz().getId().equals(quiz.getId()))
                .sorted(Comparator.comparing(Result::getScore).reversed())
                .toList();

        // Rank beramiz (1,2,3,...)
        AtomicInteger rankCounter = new AtomicInteger(1);
        results.forEach(r -> r.setRank(rankCounter.getAndIncrement()));

        return results;
    }
}
