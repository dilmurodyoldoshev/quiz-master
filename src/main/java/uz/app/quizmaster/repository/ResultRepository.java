package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.quizmaster.entity.Result;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Integer> {

    List<Result> findByQuizIdOrderByScoreDesc(Integer quizId);
}
