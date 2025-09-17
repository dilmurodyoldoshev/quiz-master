package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.LeaderboardEntryDto;
import uz.app.quizmaster.dto.ResultDto;
import uz.app.quizmaster.entity.Result;
import uz.app.quizmaster.payload.ResponseMessage;

import java.util.List;

public interface ResultService {
    ResponseMessage<Result> calculateResult(Integer attemptId);
    ResponseMessage<List<LeaderboardEntryDto>> getLeaderboard(Integer quizId);
    ResponseMessage<List<ResultDto>> getResultsForCurrentUser();
}
