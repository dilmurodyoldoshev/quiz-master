package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import uz.app.quizmaster.dto.QuestionDto;
import uz.app.quizmaster.entity.Question;
import uz.app.quizmaster.entity.Quiz;
import uz.app.quizmaster.entity.User;
import uz.app.quizmaster.helper.Helper;
import uz.app.quizmaster.payload.ResponseMessage;
import uz.app.quizmaster.repository.QuestionRepository;
import uz.app.quizmaster.repository.QuizRepository;
import uz.app.quizmaster.service.QuestionService;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage addQuestion(Integer quizId, QuestionDto dto) {
        User teacher = Helper.getCurrentPrincipal();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        if (!quiz.getCreatedBy().equals(teacher)) {
            return new ResponseMessage(false, "You are not allowed to add questions to this quiz", null);
        }

        Question question = new Question();
        mapDtoToQuestion(dto, question);
        question.setQuiz(quiz);

        Question saved = questionRepository.save(question);

        return new ResponseMessage(true, "Question successfully added", saved);
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage updateQuestion(Integer quizId, Integer questionId, QuestionDto dto) {
        User teacher = Helper.getCurrentPrincipal();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));

        if (!question.getQuiz().getId().equals(quiz.getId()) || !quiz.getCreatedBy().equals(teacher)) {
            return new ResponseMessage(false, "You are not allowed to update this question", null);
        }

        mapDtoToQuestion(dto, question);
        Question updated = questionRepository.save(question);

        return new ResponseMessage(true, "Question successfully updated", updated);
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage deleteQuestion(Integer quizId, Integer questionId) {
        User teacher = Helper.getCurrentPrincipal();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));

        if (!question.getQuiz().getId().equals(quiz.getId()) || !quiz.getCreatedBy().equals(teacher)) {
            return new ResponseMessage(false, "You are not allowed to delete this question", null);
        }

        questionRepository.delete(question);

        return new ResponseMessage(true, "Question successfully deleted", null);
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage getQuestion(Integer quizId, Integer questionId) {
        User teacher = Helper.getCurrentPrincipal();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));

        if (!question.getQuiz().getId().equals(quiz.getId()) || !quiz.getCreatedBy().equals(teacher)) {
            return new ResponseMessage(false, "You are not allowed to view this question", null);
        }

        return new ResponseMessage(true, "Question found", question);
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage getAllQuestions(Integer quizId) {
        User teacher = Helper.getCurrentPrincipal();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        if (!quiz.getCreatedBy().equals(teacher)) {
            return new ResponseMessage(false, "You are not allowed to view questions of this quiz", null);
        }

        List<Question> questions = questionRepository.findByQuizId(quizId);

        return new ResponseMessage(true, "Questions list", questions);
    }


    @Override
    public ResponseMessage getAllQuestionsPublic(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        // Faqat aktiv quizdagi savollarni olish
        if (!quiz.getIsActive()) {
            return new ResponseMessage(false, "Quiz is not active", null);
        }

        List<Question> questions = questionRepository.findAll()
                .stream()
                .filter(q -> q.getQuiz().getId().equals(quiz.getId()))
                .toList();

        return new ResponseMessage(true, "Questions list fetched successfully", questions);
    }

    @Override
    public ResponseMessage getQuestionPublic(Integer quizId, Integer questionId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

        if (!quiz.getIsActive()) {
            return new ResponseMessage(false, "Quiz is not active", null);
        }

        Question question = questionRepository.findByIdAndQuizId(questionId, quizId)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));


        return new ResponseMessage(true, "Question fetched successfully", question);
    }


    private void mapDtoToQuestion(QuestionDto dto, Question question) {
        question.setText(dto.getText());
        question.setOptionA(dto.getOptionA());
        question.setOptionB(dto.getOptionB());
        question.setOptionC(dto.getOptionC());
        question.setOptionD(dto.getOptionD());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setTimeLimit(dto.getTimeLimit());
    }
}
