package uz.app.quizmaster.service;

import uz.app.quizmaster.entity.Result;

import java.util.List;

public interface ResultService {
    Result calculateResult(Integer attemptId);
    List<Result> getLeaderboard(Integer quizId);
}
