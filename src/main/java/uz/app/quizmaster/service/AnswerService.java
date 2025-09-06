package uz.app.quizmaster.service;

import uz.app.quizmaster.entity.Answer;

public interface AnswerService {
    Answer submitAnswer(Integer questionId, Integer userId, String selectedOption);
}
