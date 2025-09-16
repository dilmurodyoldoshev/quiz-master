package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.QuestionDto;
import uz.app.quizmaster.entity.Question;
import uz.app.quizmaster.payload.ResponseMessage;

import java.util.List;

public interface QuestionService {

    // Teacher uchun metodlar
    ResponseMessage<Question> addQuestion(Integer quizId, QuestionDto dto);
    ResponseMessage<Question> updateQuestion(Integer quizId, Integer questionId, QuestionDto dto);
    ResponseMessage<Question> deleteQuestion(Integer quizId, Integer questionId);
    ResponseMessage<Question> getQuestion(Integer quizId, Integer questionId);
    ResponseMessage<List<Question>> getAllQuestions(Integer quizId);

    // Public (hammaga ko‘rinadigan) metodlar
    ResponseMessage<List<QuestionDto>> getAllQuestionsPublic(Integer quizId);
    ResponseMessage<QuestionDto> getQuestionPublic(Integer quizId, Integer questionId);
}
