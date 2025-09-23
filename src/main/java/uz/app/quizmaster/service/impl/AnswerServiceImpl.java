package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.app.quizmaster.dto.AnswerDto;
import uz.app.quizmaster.entity.Answer;
import uz.app.quizmaster.entity.Attempt;
import uz.app.quizmaster.entity.Question;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.enums.AnswerType;
import uz.app.quizmaster.helper.Helper;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.AnswerRepository;
import uz.app.quizmaster.repository.AttemptRepository;
import uz.app.quizmaster.repository.QuestionRepository;
import uz.app.quizmaster.service.AnswerService;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;

    @Override
    @Transactional
    public ResponseMessage<AnswerDto> submitAnswer(Integer attemptId, Integer questionId, AnswerType selected) {
        try {
            User student = Helper.getCurrentPrincipal();

            if (selected == null) {
                return ResponseMessage.fail("Selected option is required", null);
            }

            // Attempt tekshirish
            Attempt attempt = attemptRepository.findById(attemptId)
                    .orElseThrow(() -> new NoSuchElementException("Attempt not found"));

            if (!Objects.equals(attempt.getUser().getId(), student.getId())) {
                return ResponseMessage.fail("You are not allowed to submit answer for this attempt", null);
            }
            if (attempt.getFinishedAt() != null) {
                return ResponseMessage.fail("Attempt is already finished", null);
            }

            LocalDateTime now = LocalDateTime.now();
            if (attempt.getDeadline() != null && now.isAfter(attempt.getDeadline())) {
                return ResponseMessage.fail("Attempt deadline has passed", null);
            }

            Quiz quiz = attempt.getQuiz();
            if (!Boolean.TRUE.equals(quiz.getIsActive())) {
                return ResponseMessage.fail("Quiz is not active", null);
            }

            // Question
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new NoSuchElementException("Question not found"));

            if (question.getQuiz() == null || !Objects.equals(question.getQuiz().getId(), quiz.getId())) {
                return ResponseMessage.fail("Question does not belong to this quiz/attempt", null);
            }

            // Old answer bor-yo‘qligini tekshirish
            Optional<Answer> existingAnswerOpt = answerRepository.findByAttemptIdAndQuestionId(attemptId, questionId);

            Answer answer = existingAnswerOpt.orElseGet(Answer::new);
            boolean wasCorrect = Boolean.TRUE.equals(answer.getIsCorrect());

            // update fields
            answer.setQuestion(question);
            answer.setUser(student);
            answer.setAttempt(attempt);
            answer.setSelectedOption(selected);

            boolean correct = question.getCorrectAnswer() != null && question.getCorrectAnswer().equals(selected);
            answer.setIsCorrect(correct);
            answer.setAnsweredAt(now);

            Answer saved = answerRepository.save(answer);

            // Score update
            if (wasCorrect != correct) {
                int score = attempt.getScore() == null ? 0 : attempt.getScore();
                if (wasCorrect && !correct) score--;
                else if (!wasCorrect && correct) score++;
                attempt.setScore(score);
                attemptRepository.save(attempt);
            }

            // ✅ Mapping Answer → AnswerDto
            AnswerDto dto = new AnswerDto();
            dto.setQuestionId(saved.getQuestion().getId());
            dto.setQuestionText(saved.getQuestion().getText());
            dto.setSelectedOption(saved.getSelectedOption());
            dto.setIsCorrect(saved.getIsCorrect());

            return ResponseMessage.success("Answer submitted successfully", dto);

        } catch (NoSuchElementException ex) {
            log.warn("Submit answer failed: {}", ex.getMessage());
            return ResponseMessage.fail(ex.getMessage(), null);
        } catch (Exception e) {
            log.error("Error submitting answer: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error submitting answer: " + e.getMessage(), null);
        }
    }
}
