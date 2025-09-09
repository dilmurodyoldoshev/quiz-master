package uz.app.quizmaster.service;

import uz.app.quizmaster.payload.ResponseMessage;

public interface AnswerService {
    ResponseMessage submitAnswer(Integer questionId, String selectedOption);
}
