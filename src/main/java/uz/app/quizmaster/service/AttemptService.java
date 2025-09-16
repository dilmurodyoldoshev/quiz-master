package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.AttemptDto;
import uz.app.quizmaster.payload.ResponseMessage;

public interface AttemptService {
    ResponseMessage<AttemptDto> startAttempt(Integer quizId);      // Hozirgi student uchun
    ResponseMessage<AttemptDto> finishAttempt(Integer attemptId);  // Hozirgi student uchun
}
