package uz.app.quizmaster.service;

import uz.app.quizmaster.dto.QuizDto;
import uz.app.quizmaster.payload.ResponseMessage;

public interface QuizService {
    // Teacher uchun metodlar
    ResponseMessage createQuiz(QuizDto quizDto);
    ResponseMessage activateQuiz(Integer quizId);
    ResponseMessage finishQuiz(Integer quizId);
    ResponseMessage toggleCheatingControl(Integer quizId, Boolean enabled);
    ResponseMessage getAllQuizzes();
    ResponseMessage getQuizById(Integer quizId);

    // Public (hammaga ko‘rinadigan) metodlar
    ResponseMessage getAllQuizzesPublic();
    ResponseMessage getQuizByIdPublic(Integer quizId);
}
