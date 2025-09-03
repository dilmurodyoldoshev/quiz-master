package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.quizmaster.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
}
