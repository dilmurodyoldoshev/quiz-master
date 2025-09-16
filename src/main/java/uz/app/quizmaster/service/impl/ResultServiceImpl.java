package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.dto.LeaderboardEntryDto;
import uz.app.quizmaster.entity.Attempt;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.Result;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.AttemptRepository;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.repository.ResultRepository;
import uz.app.quizmaster.service.ResultService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final AttemptRepository attemptRepository;
    private final QuizRepository quizRepository;

    @Override
    public ResponseMessage<Result> calculateResult(Integer attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new NoSuchElementException("Attempt not found"));

        Result existing = resultRepository.findByQuizIdOrderByScoreDesc(attempt.getQuiz().getId())
                .stream()
                .filter(r -> r.getUser().getId().equals(attempt.getUser().getId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return ResponseMessage.success("Result already exists", existing);
        }

        Result result = new Result();
        result.setQuiz(attempt.getQuiz());
        result.setUser(attempt.getUser());
        result.setScore(attempt.getScore());
        result.setCompletedAt(LocalDateTime.now());

        Result saved = resultRepository.save(result);
        return ResponseMessage.success("Result calculated successfully", saved);
    }

    @Override
    public ResponseMessage<List<LeaderboardEntryDto>> getLeaderboard(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        // Barcha attempts
        List<Attempt> attempts = attemptRepository.findByQuizId(quizId);

        // Har bir user uchun eng yuqori balli attempt
        List<Attempt> bestAttempts = attempts.stream()
                .filter(a -> a.getFinishedAt() != null) // faqat tugallanganlar
                .collect(Collectors.toMap(a -> a.getUser().getId(), Function.identity(), BinaryOperator.maxBy((a1, a2) -> {
                    int cmp = Integer.compare(a1.getScore(), a2.getScore());
                    if (cmp == 0) {
                        return a1.getFinishedAt().compareTo(a2.getFinishedAt());
                    }
                    return cmp;
                })))
                .values()
                .stream()
                .toList();

        // Sorting: score DESC, finishedAt ASC
        List<Attempt> sorted = bestAttempts.stream()
                .sorted((a1, a2) -> {
                    int cmp = Integer.compare(a2.getScore(), a1.getScore()); // DESC
                    if (cmp == 0) {
                        return a1.getFinishedAt().compareTo(a2.getFinishedAt()); // ASC
                    }
                    return cmp;
                })
                .toList();

        // Rank berish
        AtomicInteger counter = new AtomicInteger(1);
        List<LeaderboardEntryDto> leaderboard = sorted.stream()
                .map(a -> new LeaderboardEntryDto(
                        a.getUser().getUsername(),
                        a.getScore(),
                        counter.getAndIncrement()
                ))
                .toList();

        return ResponseMessage.success("Leaderboard fetched successfully", leaderboard);
    }
}
