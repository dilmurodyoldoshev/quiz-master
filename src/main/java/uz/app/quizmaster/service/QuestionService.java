package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.QuestionDto;
import uz.app.quizmaster.payload.ResponseMessage;

public interface QuestionService {
    // Teacher uchun metodlar
    ResponseMessage addQuestion(Integer quizId, QuestionDto dto);
    ResponseMessage updateQuestion(Integer quizId, Integer questionId, QuestionDto dto);
    ResponseMessage deleteQuestion(Integer quizId, Integer questionId);
    ResponseMessage getQuestion(Integer quizId, Integer questionId);
    ResponseMessage getAllQuestions(Integer quizId);

    // Public (hammaga ko‘rinadigan) metodlar
    ResponseMessage getAllQuestionsPublic(Integer quizId);
    ResponseMessage getQuestionPublic(Integer quizId, Integer questionId);
}
