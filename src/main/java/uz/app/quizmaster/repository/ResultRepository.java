package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.Result;
import uz.app.quizmaster.entity.User;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Integer> {

    List<Result> findByUser(User student);

    List<Result> findByQuiz(Quiz quiz);

    List<Result> findByQuizOrderByScoreDesc(Quiz quiz);
}
