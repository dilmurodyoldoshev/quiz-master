package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.AttemptDto;
import uz.app.quizmaster.payload.ResponseMessage;

import java.util.List;
import java.util.Map;

public interface AttemptService {
    ResponseMessage<AttemptDto> startAttempt(Integer quizId);      // Hozirgi student uchun
    ResponseMessage<AttemptDto> finishAttempt(Integer attemptId, Map<Integer, String> answersMap);  // Hozirgi student uchun
    ResponseMessage<List<AttemptDto>> getAttempts();
}
