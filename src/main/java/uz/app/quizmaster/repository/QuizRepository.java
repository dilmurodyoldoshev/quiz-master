package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.quizmaster.entity.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {
}
