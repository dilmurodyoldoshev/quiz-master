package uz.app.quizmaster.service;

import uz.app.quizmaster.entity.Quiz;
import java.util.List;

public interface QuizService {
    Quiz createQuiz(Quiz quiz, Integer teacherId);
    Quiz activateQuiz(Integer quizId);
    Quiz finishQuiz(Integer quizId);
    Quiz toggleCheatingControl(Integer quizId, Boolean enabled);
    List<Quiz> getAllQuizzes();
    Quiz getQuizById(Integer id);
}
