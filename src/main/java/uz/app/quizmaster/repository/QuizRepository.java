package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.quizmaster.entity.Question;

public interface QuizRepository extends JpaRepository<Question, Integer> {
}
