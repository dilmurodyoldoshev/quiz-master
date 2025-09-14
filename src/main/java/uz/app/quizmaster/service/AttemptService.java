package uz.app.quizmaster.service;

import uz.app.quizmaster.entity.Attempt;
import uz.app.quizmaster.payload.ResponseMessage;

public interface AttemptService {
    ResponseMessage<Attempt> startAttempt(Integer quizId);      // Hozirgi student uchun
    ResponseMessage<Attempt> finishAttempt(Integer attemptId);  // Hozirgi student uchun
}
