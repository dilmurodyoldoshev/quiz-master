package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.AnswerDto;
import uz.app.quizmaster.payload.ResponseMessage;

public interface AnswerService {
    ResponseMessage<AnswerDto> submitAnswer(Integer attemptId, Integer questionId, String selectedOption);
}
