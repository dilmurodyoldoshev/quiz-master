package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.dto.LeaderboardEntryDto;
import uz.app.quizmaster.dto.ResultDto;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.Result;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.helper.Helper;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.ResultRepository;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.service.ResultService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final QuizRepository quizRepository;

    // 🔹 TEACHER: o‘z quizlariga tushgan natijalarni ko‘rish
    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<List<LeaderboardEntryDto>> getResultsForMyQuizzes(Integer quizId) {
        User teacher = Helper.getCurrentPrincipal();

        // 1. Quizni olish
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // 2. Teacher o‘zi yaratganmi tekshirish
        if (!quiz.getCreatedBy().getId().equals(teacher.getId())) {
            return ResponseMessage.fail("You can only view results for your own quizzes", null);
        }

        // 3. Resultlarni olish
        List<Result> results = resultRepository.findByQuizOrderByScoreDesc(quiz);

        // 4. DTO mapping + rank hisoblash
        List<LeaderboardEntryDto> leaderboard = new ArrayList<>();
        int rank = 1;
        for (Result r : results) {
            LeaderboardEntryDto dto = new LeaderboardEntryDto();
            dto.setUsername(r.getUser().getUsername());
            dto.setScore(r.getScore());
            dto.setRank(rank++);
            leaderboard.add(dto);
        }

        return ResponseMessage.success("Leaderboard fetched", leaderboard);
    }

    // 🔹 STUDENT: o‘zining natijalarini ko‘rish
    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseMessage<List<ResultDto>> getMyResults() {
        try {
            User student = Helper.getCurrentPrincipal();
            List<Result> results = resultRepository.findByUser(student);

            List<ResultDto> resultDtos = results.stream()
                    .map(r -> new ResultDto(
                            r.getQuiz().getId(),
                            r.getQuiz().getTitle(),
                            r.getScore(),
                            r.getCompletedAt(),
                            r.getRank()
                    ))
                    .toList();

            return ResponseMessage.success("Your results fetched successfully", resultDtos);
        } catch (Exception e) {
            log.error("Error fetching student results: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error fetching your results", null);
        }
    }

    // 🔹 STUDENT: Leaderboard (quiz bo‘yicha umumiy natijalar va ranklar)
    @Override
    public ResponseMessage<List<LeaderboardEntryDto>> getLeaderboardForQuiz(Integer quizId) {
        try {
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));

            List<Result> results = resultRepository.findByQuiz(quiz);

            // score bo‘yicha tartiblash
            List<LeaderboardEntryDto> leaderboard = results.stream()
                    .sorted(Comparator.comparing(Result::getScore).reversed())
                    .map(r -> new LeaderboardEntryDto(
                            r.getUser().getUsername(),
                            r.getScore(),
                            r.getRank()
                    ))
                    .toList();

            return ResponseMessage.success("Leaderboard fetched successfully", leaderboard);
        } catch (Exception e) {
            log.error("Error fetching leaderboard: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error fetching leaderboard", null);
        }
    }
}
