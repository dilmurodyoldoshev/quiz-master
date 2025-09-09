package uz.app.quizmaster.service;

import uz.app.quizmaster.payload.ResponseMessage;

public interface ResultService {
    ResponseMessage calculateResult(Integer attemptId);
    ResponseMessage getLeaderboard(Integer quizId);
}
