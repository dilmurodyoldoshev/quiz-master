package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.entity.Answer;
import uz.app.quizmaster.entity.Question;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.repository.AnswerRepository;
import uz.app.quizmaster.repository.QuestionRepository;
import uz.app.quizmaster.repository.UserRepository;
import uz.app.quizmaster.service.AnswerService;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Override
    public Answer submitAnswer(Integer questionId, Integer userId, String selectedOption) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setUser(user);
        answer.setSelectedOption(selectedOption);
        answer.setIsCorrect(selectedOption != null &&
                selectedOption.equalsIgnoreCase(question.getCorrectAnswer()));
        answer.setAnsweredAt(LocalDateTime.now());

        return answerRepository.save(answer);
    }
}
