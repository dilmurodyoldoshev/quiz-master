package uz.app.quizmaster.service;

import uz.app.quizmaster.entity.Attempt;

public interface AttemptService {
    Attempt startAttempt(Integer quizId, Integer userId);
    Attempt finishAttempt(Integer attemptId);
}
