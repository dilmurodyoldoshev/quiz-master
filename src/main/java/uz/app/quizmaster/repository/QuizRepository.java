package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.User;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    List<Quiz> findByCreatedBy(User teacher);

    List<Quiz> findByIsActiveTrue();

    Optional<Quiz> findByIdAndIsActiveTrue(Integer quizId);
}
