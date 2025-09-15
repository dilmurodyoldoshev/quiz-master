package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.service.AnswerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;

    @Override
    @Transactional
    public ResponseMessage<Answer> submitAnswer(Integer attemptId, Integer questionId, String selectedOption) {
        try {
            // Current student
            User student = Helper.getCurrentPrincipal();

            // Validate selected option presence
            if (selectedOption == null || selectedOption.trim().isEmpty()) {
                return ResponseMessage.fail("Selected option is required", null);
            }

            // Normalize and parse selectedOption to enum (A/B/C/D)
            AnswerType selected;
            try {
                selected = AnswerType.valueOf(selectedOption.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return ResponseMessage.fail("Invalid selected option. Allowed values: A, B, C, D", null);
            }

            // Get attempt
            Attempt attempt = attemptRepository.findById(attemptId)
                    .orElseThrow(() -> new NoSuchElementException("Attempt not found"));

            // Check attempt ownership
            if (!Objects.equals(attempt.getUser().getId(), student.getId())) {
                return ResponseMessage.fail("You are not allowed to submit answer for this attempt", null);
            }

            // Check finished
            if (attempt.getFinishedAt() != null) {
                return ResponseMessage.fail("Attempt is already finished", null);
            }

            Quiz quiz = attempt.getQuiz();

            // Check quiz active
            if (!Boolean.TRUE.equals(quiz.getIsActive())) {
                return ResponseMessage.fail("Quiz is not active", null);
            }

            // Get question
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new NoSuchElementException("Question not found"));

            // Ensure question belongs to the same quiz as the attempt
            if (question.getQuiz() == null || !Objects.equals(question.getQuiz().getId(), quiz.getId())) {
                return ResponseMessage.fail("Question does not belong to this quiz/attempt", null);
            }

            // Check duplicate
            Optional<Answer> existingAnswer = answerRepository.findByAttemptIdAndQuestionId(attemptId, questionId);
            if (existingAnswer.isPresent()) {
                return ResponseMessage.fail("You have already answered this question", existingAnswer.get());
            }

            // Create answer
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setUser(student);
            // store selected option as string (e.g., "A")
            answer.setSelectedOption(selected.name());
            // compare enum values
            boolean correct = question.getCorrectAnswer() != null && question.getCorrectAnswer().equals(selected);
            answer.setIsCorrect(correct);
            answer.setAnsweredAt(LocalDateTime.now());
            answer.setAttempt(attempt);

            Answer saved = answerRepository.save(answer);

            // Update attempt score
            List<Answer> attemptAnswers = answerRepository.findByAttemptId(attemptId);
            int score = (int) attemptAnswers.stream()
                    .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                    .count();
            attempt.setScore(score);
            attemptRepository.save(attempt);

            return ResponseMessage.success("Answer submitted successfully", saved);
        } catch (NoSuchElementException ex) {
            log.warn("Submit answer failed: {}", ex.getMessage());
            return ResponseMessage.fail(ex.getMessage(), null);
        } catch (Exception e) {
            log.error("Error submitting answer: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error submitting answer: " + e.getMessage(), null);
        }
    }
}
