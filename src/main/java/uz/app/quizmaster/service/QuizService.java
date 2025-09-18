package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.QuizDto;
import uz.app.quizmaster.payload.ResponseMessage;

import java.util.List;

public interface QuizService {

    // Teacher uchun metodlar
    ResponseMessage<QuizDto> createQuiz(QuizDto quizDto);
    ResponseMessage<QuizDto> activateQuiz(Integer quizId);
    ResponseMessage<QuizDto> finishQuiz(Integer quizId);
    ResponseMessage<QuizDto> toggleCheatingControl(Integer quizId, Boolean enabled);
    ResponseMessage<List<QuizDto>> getAllQuizzes();
    ResponseMessage<QuizDto> getQuizById(Integer quizId);

    // Public (hammaga ko‘rinadigan) metodlar
    ResponseMessage<List<QuizDto>> getAllQuizzesPublic();
    ResponseMessage<QuizDto> getQuizByIdPublic(Integer quizId);
}
