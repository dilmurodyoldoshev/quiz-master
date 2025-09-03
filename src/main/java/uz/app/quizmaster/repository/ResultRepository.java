package uz.app.quizmaster.repository;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import uz.app.quizmaster.entity.Result;

public interface ResultRepository extends JpaRepositoryImplementation<Result, Integer> {
}
