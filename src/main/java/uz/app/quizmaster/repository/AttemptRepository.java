package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.quizmaster.entity.Attempt;

import java.util.Optional;

public interface AttemptRepository extends JpaRepository<Attempt, Integer> {

    // Oxirgi attempt (agar ko‘p bo‘lsa ham eng so‘nggisi)
    Optional<Attempt> findFirstByUserIdAndQuizIdOrderByStartedAtDesc(Integer userId, Integer quizId);

    // Faol attempt bor-yo‘qligini tekshirish uchun (kerak bo‘lsa)
    boolean existsByUserIdAndQuizIdAndFinishedAtIsNull(Integer userId, Integer quizId);
}
