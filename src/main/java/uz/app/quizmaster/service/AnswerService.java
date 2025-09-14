package uz.app.quizmaster.service;

import uz.app.quizmaster.entity.Answer;
import uz.app.quizmaster.payload.ResponseMessage;

public interface AnswerService {
    ResponseMessage<Answer> submitAnswer(Integer attemptId, Integer questionId, String selectedOption);
}
