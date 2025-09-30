package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.quizmaster.entity.Attempt;

import java.util.List;
import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Integer> {

    // Oxirgi attempt (agar ko‘p bo‘lsa ham eng so‘nggisi)
    Optional<Attempt> findFirstByUserIdAndQuizIdOrderByStartedAtDesc(Integer userId, Integer quizId);

    List<Attempt> findByUserIdOrderByStartedAtDesc(Integer id);
}
