package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.quizmaster.entity.Attempt;

public interface AttemptRepository extends JpaRepository<Attempt, Integer> {
}
