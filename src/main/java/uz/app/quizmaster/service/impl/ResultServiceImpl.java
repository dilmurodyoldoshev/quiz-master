package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.dto.LeaderboardEntryDto;
import uz.app.quizmaster.dto.ResultDto;
import uz.app.quizmaster.entity.Attempt;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.Result;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.helper.Helper;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.AttemptRepository;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.repository.ResultRepository;
import uz.app.quizmaster.service.ResultService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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

    @Override
    public ResponseMessage<List<ResultDto>> getResultsForCurrentUser() {
        try {
            User me = Helper.getCurrentPrincipal();
            if (me == null) return ResponseMessage.fail("User not authenticated", null);

            // Foydalanuvchining barcha natijalari (oxirgi tugallangan bo'yicha tartiblash)
            List<Result> results = resultRepository.findByUserIdOrderByCompletedAtDesc(me.getId());

            // Agar rank ham kerak bo'lsa, har bir quiz uchun leaderboard olib, pozitsiyasini hisoblang.
            // Bu yondashuv oddiy va tushunarli: quiz bo'yicha barcha resultlarni olamiz va index topamiz.
            Map<Integer, List<Result>> byQuiz = results.stream()
                    .collect(Collectors.groupingBy(r -> r.getQuiz().getId()));

            List<ResultDto> dtoList = results.stream().map(r -> {
                ResultDto dto = new ResultDto();
                dto.setQuizId(r.getQuiz().getId());
                dto.setQuizTitle(r.getQuiz().getTitle());
                dto.setScore(r.getScore());
                dto.setCompletedAt(r.getCompletedAt());

                // rank hisoblash (agar agar resultRepository.findByQuizIdOrderByScoreDesc mavjud bo'lsa)
                List<Result> leaderboard = resultRepository.findByQuizIdOrderByScoreDesc(r.getQuiz().getId());
                AtomicInteger rankCounter = new AtomicInteger(1);
                Integer rank = leaderboard.stream()
                        .filter(lr -> {
                            boolean same = lr.getUser().getId().equals(me.getId());
                            if (!same) rankCounter.incrementAndGet();
                            return same;
                        })
                        .findFirst()
                        .map(x -> rankCounter.get() - 1) // because incremented after non-matching entries
                        .orElse(null);

                // Above logic is a bit awkward with AtomicInteger; simpler:
                if (rank == null) {
                    for (int i = 0; i < leaderboard.size(); i++) {
                        if (leaderboard.get(i).getUser().getId().equals(me.getId())) {
                            rank = i + 1;
                            break;
                        }
                    }
                }

                dto.setRank(rank);
                return dto;
            }).collect(Collectors.toList());

            return ResponseMessage.success("User results fetched successfully", dtoList);
        } catch (Exception e) {
            return ResponseMessage.fail("Error fetching user results: " + e.getMessage(), null);
        }
    }
}
