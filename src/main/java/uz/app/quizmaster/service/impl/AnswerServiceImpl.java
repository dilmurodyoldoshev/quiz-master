package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.app.quizmaster.entity.Answer;
import uz.app.quizmaster.entity.Attempt;
import uz.app.quizmaster.entity.Question;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.User;
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

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AttemptRepository attemptRepository;

    @Override
    @Transactional
    public ResponseMessage<Answer> submitAnswer(Integer attemptId, Integer questionId, String selectedOption) {
        // 🔹 Current student
        User student = Helper.getCurrentPrincipal();

        // 🔹 Attemptni olish
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new NoSuchElementException("Attempt not found"));

        if (!attempt.getUser().getId().equals(student.getId())) {
            return ResponseMessage.fail("You are not allowed to submit answer for this attempt", null);
        }

        if (attempt.getFinishedAt() != null) {
            return ResponseMessage.fail("Attempt is already finished", null);
        }

        Quiz quiz = attempt.getQuiz();

        if (!Boolean.TRUE.equals(quiz.getIsActive())) {
            return ResponseMessage.fail("Quiz is not active", null);
        }

        // 🔹 Savolni olish
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));

        // 🔹 Duplicate javobni tekshirish
        Optional<Answer> existingAnswer = answerRepository
                .findByAttemptIdAndQuestionId(attemptId, questionId);
        if (existingAnswer.isPresent()) {
            return ResponseMessage.fail("You have already answered this question", existingAnswer.get());
        }

        // 🔹 Javobni yaratish
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setUser(student);
        answer.setSelectedOption(selectedOption);
        answer.setIsCorrect(selectedOption != null && selectedOption.equalsIgnoreCase(question.getCorrectAnswer()));
        answer.setAnsweredAt(LocalDateTime.now());
        answer.setAttempt(attempt);

        Answer saved = answerRepository.save(answer);

        // 🔹 Current attempt score update
        List<Answer> attemptAnswers = answerRepository.findByAttemptId(attemptId);
        int score = (int) attemptAnswers.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                .count();
        attempt.setScore(score);
        attemptRepository.save(attempt);

        return ResponseMessage.success("Answer submitted successfully", saved);
    }
}
