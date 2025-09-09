package uz.app.quizmaster.service;

import uz.app.quizmaster.payload.ResponseMessage;

public interface AttemptService {
    ResponseMessage startAttempt(Integer quizId);      // current student uchun
    ResponseMessage finishAttempt(Integer attemptId);  // current student uchun
}
