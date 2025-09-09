package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.quizmaster.entity.Answer;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    // Foydalanuvchining ma'lum quizdagi barcha javoblari
    List<Answer> findByUserIdAndQuestionQuizId(Integer userId, Integer quizId);
}
