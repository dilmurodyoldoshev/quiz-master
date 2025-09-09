package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.quizmaster.entity.Result;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Integer> {

    List<Result> findByQuizIdOrderByScoreDesc(Integer quizId);
}
