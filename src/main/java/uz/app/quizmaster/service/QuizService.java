package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.QuizDto;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.payload.ResponseMessage;

import java.util.List;

public interface QuizService {

    // Teacher uchun metodlar
    ResponseMessage<Quiz> createQuiz(QuizDto quizDto);
    ResponseMessage<Quiz> activateQuiz(Integer quizId);
    ResponseMessage<Quiz> finishQuiz(Integer quizId);
    ResponseMessage<Quiz> toggleCheatingControl(Integer quizId, Boolean enabled);
    ResponseMessage<List<Quiz>> getAllQuizzes();
    ResponseMessage<Quiz> getQuizById(Integer quizId);

    // Public (hammaga ko‘rinadigan) metodlar
    ResponseMessage<List<QuizDto>> getAllQuizzesPublic();
    ResponseMessage<QuizDto> getQuizByIdPublic(Integer quizId);
}
