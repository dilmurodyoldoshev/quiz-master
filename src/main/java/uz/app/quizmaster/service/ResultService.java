package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.LeaderboardEntryDto;
import uz.app.quizmaster.dto.ResultDto;
import uz.app.quizmaster.payload.ResponseMessage;

import java.util.List;

public interface ResultService {
    // Teacher o‘zining quizlariga tushgan barcha natijalar
    ResponseMessage<List<LeaderboardEntryDto>> getResultsForMyQuizzes(Integer quizId);

    // Student o‘zining natijalari
    ResponseMessage<List<ResultDto>> getMyResults();

    // Student o‘zining leaderboarddagi o‘rnini ko‘rishi
    ResponseMessage<List<LeaderboardEntryDto>> getLeaderboardForQuiz(Integer quizId);
}
