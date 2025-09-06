package uz.app.quizmaster.service;

import uz.app.quizmaster.entity.Question;

import java.util.List;

public interface QuestionService {
    Question addQuestion(Integer quizId, Question question, Integer teacherId);
    Question updateQuestion(Integer questionId, Question question);
    void deleteQuestion(Integer questionId);
    List<Question> getQuestionsByQuiz(Integer quizId);
}
