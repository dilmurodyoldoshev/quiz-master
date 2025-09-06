package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.entity.Question;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.repository.QuestionRepository;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.repository.UserRepository;
import uz.app.quizmaster.service.QuestionService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Override
    public Question addQuestion(Integer quizId, Question question, Integer teacherId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new NoSuchElementException("Teacher not found"));

        question.setQuiz(quiz);
        question.setCreatedBy(teacher);

        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Integer questionId, Question updatedQuestion) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));

        question.setText(updatedQuestion.getText());
        question.setOptionA(updatedQuestion.getOptionA());
        question.setOptionB(updatedQuestion.getOptionB());
        question.setOptionC(updatedQuestion.getOptionC());
        question.setOptionD(updatedQuestion.getOptionD());
        question.setCorrectAnswer(updatedQuestion.getCorrectAnswer());
        question.setTimeLimit(updatedQuestion.getTimeLimit());

        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Integer questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new NoSuchElementException("Question not found");
        }
        questionRepository.deleteById(questionId);
    }

    @Override
    public List<Question> getQuestionsByQuiz(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        return questionRepository.findAll()
                .stream()
                .filter(q -> q.getQuiz().getId().equals(quiz.getId()))
                .collect(Collectors.toList());
    }
}
