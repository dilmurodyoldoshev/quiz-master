package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final AttemptRepository attemptRepository;
    private final QuizRepository quizRepository;

    @Override
    public ResponseMessage calculateResult(Integer attemptId) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new NoSuchElementException("Attempt not found"));

        Quiz quiz = attempt.getQuiz();

        // allaqachon mavjud bo‘lsa
        Result existing = resultRepository.findByQuizIdOrderByScoreDesc(quiz.getId())
                .stream()
                .filter(r -> r.getUser().getId().equals(attempt.getUser().getId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return new ResponseMessage(true, "Result already exists", existing);
        }

        Result result = new Result();
        result.setQuiz(quiz);
        result.setUser(attempt.getUser());
        result.setScore(attempt.getScore());
        result.setCompletedAt(LocalDateTime.now());

        Result saved = resultRepository.save(result);

        return new ResponseMessage(true, "Result calculated successfully", saved);
    }

    @Override
    public ResponseMessage getLeaderboard(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        List<Result> results = resultRepository.findByQuizIdOrderByScoreDesc(quiz.getId());

        // Rank beramiz
        AtomicInteger rankCounter = new AtomicInteger(1);
        results.forEach(r -> r.setRank(rankCounter.getAndIncrement()));

        return new ResponseMessage(true, "Leaderboard fetched successfully", results);
    }
}
