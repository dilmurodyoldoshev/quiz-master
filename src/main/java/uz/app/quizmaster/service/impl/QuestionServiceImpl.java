package uz.app.quizmaster.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<Question> addQuestion(Integer quizId, QuestionDto dto) {
        try {
            // validate DTO
            ResponseMessage<Question> dtoValidation = validateDto(dto);
            if (!dtoValidation.success()) return dtoValidation;

            User teacher = Helper.getCurrentPrincipal();

            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

            // compare by id to avoid proxy/equals issues
            if (quiz.getCreatedBy() == null || !Objects.equals(quiz.getCreatedBy().getId(), teacher.getId())) {
                return ResponseMessage.fail("You are not allowed to add questions to this quiz", null);
            }

            Question question = new Question();
            mapDtoToQuestion(dto, question);
            question.setQuiz(quiz);
            question.setCreatedBy(teacher); // IMPORTANT
            Question saved = questionRepository.save(question);

            return ResponseMessage.success("Question successfully added", saved);
        } catch (Exception e) {
            log.error("Error adding question: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error adding question", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<Question> updateQuestion(Integer quizId, Integer questionId, QuestionDto dto) {
        try {
            ResponseMessage<Question> dtoValidation = validateDto(dto);
            if (!dtoValidation.success()) return dtoValidation;

            User teacher = Helper.getCurrentPrincipal();
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new NoSuchElementException("Quiz not found"));
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new NoSuchElementException("Question not found"));

            if (!Objects.equals(question.getQuiz().getId(), quiz.getId())
                    || quiz.getCreatedBy() == null
                    || !Objects.equals(quiz.getCreatedBy().getId(), teacher.getId())) {
                return ResponseMessage.fail("You are not allowed to update this question", null);
            }

            mapDtoToQuestion(dto, question);
            Question updated = questionRepository.save(question);

            return ResponseMessage.success("Question successfully updated", updated);
        } catch (Exception e) {
            log.error("Error updating question: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error updating question", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<Question> deleteQuestion(Integer quizId, Integer questionId) {
        try {
            User teacher = Helper.getCurrentPrincipal();
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new NoSuchElementException("Quiz not found"));
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new NoSuchElementException("Question not found"));

            if (!Objects.equals(question.getQuiz().getId(), quiz.getId())
                    || quiz.getCreatedBy() == null
                    || !Objects.equals(quiz.getCreatedBy().getId(), teacher.getId())) {
                return ResponseMessage.fail("You are not allowed to delete this question", null);
            }

            questionRepository.delete(question);
            return ResponseMessage.success("Question successfully deleted", null);
        } catch (Exception e) {
            log.error("Error deleting question: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error deleting question", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<Question> getQuestion(Integer quizId, Integer questionId) {
        try {
            User teacher = Helper.getCurrentPrincipal();
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new NoSuchElementException("Quiz not found"));
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new NoSuchElementException("Question not found"));

            if (!Objects.equals(question.getQuiz().getId(), quiz.getId())
                    || quiz.getCreatedBy() == null
                    || !Objects.equals(quiz.getCreatedBy().getId(), teacher.getId())) {
                return ResponseMessage.fail("You are not allowed to view this question", null);
            }

            return ResponseMessage.success("Question found", question);
        } catch (Exception e) {
            log.error("Error fetching question: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error fetching question", null);
        }
    }

    @Override
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseMessage<List<Question>> getAllQuestions(Integer quizId) {
        try {
            User teacher = Helper.getCurrentPrincipal();
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

            if (quiz.getCreatedBy() == null || !Objects.equals(quiz.getCreatedBy().getId(), teacher.getId())) {
                return ResponseMessage.fail("You are not allowed to view questions of this quiz", null);
            }

            List<Question> questions = questionRepository.findByQuizId(quizId);
            return ResponseMessage.success("Questions list", questions);
        } catch (Exception e) {
            log.error("Error fetching questions: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error fetching questions", null);
        }
    }

    @Override
    public ResponseMessage<List<QuestionDto>> getAllQuestionsPublic(Integer quizId) {
        try {
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

            if (!quiz.getIsActive()) {
                return ResponseMessage.fail("Quiz is not active", null);
            }

            List<Question> questions = questionRepository.findByQuizId(quizId);

            // entity → dto mapping
            List<QuestionDto> dtoList = questions.stream().map(this::mapToDto).toList();

            return ResponseMessage.success("Questions list fetched successfully", dtoList);
        } catch (Exception e) {
            log.error("Error fetching public questions: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error fetching public questions", null);
        }
    }

    @Override
    public ResponseMessage<QuestionDto> getQuestionPublic(Integer quizId, Integer questionId) {
        try {
            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> new NoSuchElementException("Quiz not found"));

            if (!quiz.getIsActive()) {
                return ResponseMessage.fail("Quiz is not active", null);
            }

            Question question = questionRepository.findByIdAndQuizId(questionId, quizId)
                    .orElseThrow(() -> new NoSuchElementException("Question not found"));

            return ResponseMessage.success("Question fetched successfully", mapToDto(question));
        } catch (Exception e) {
            log.error("Error fetching public question: {}", e.getMessage(), e);
            return ResponseMessage.fail("Error fetching public question", null);
        }
    }

    private QuestionDto mapToDto(Question question) {
        QuestionDto dto = new QuestionDto();
        dto.setText(question.getText());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        // ⚠️ E’tibor: studentga correctAnswer qaytmaydi!
        return dto;
    }


    private void mapDtoToQuestion(QuestionDto dto, Question question) {
        question.setText(dto.getText());
        question.setOptionA(dto.getOptionA());
        question.setOptionB(dto.getOptionB());
        question.setOptionC(dto.getOptionC());
        question.setOptionD(dto.getOptionD());
        question.setCorrectAnswer(dto.getCorrectAnswer());
    }

    private ResponseMessage<Question> validateDto(QuestionDto dto) {
        if (dto == null) {
            return ResponseMessage.fail("Request body is empty", null);
        }
        if (dto.getText() == null || dto.getText().trim().isEmpty()) {
            return ResponseMessage.fail("Question text is required", null);
        }
        if (dto.getOptionA() == null || dto.getOptionA().trim().isEmpty()) {
            return ResponseMessage.fail("Option A is required", null);
        }
        if (dto.getOptionB() == null || dto.getOptionB().trim().isEmpty()) {
            return ResponseMessage.fail("Option B is required", null);
        }
        if (dto.getOptionC() == null || dto.getOptionC().trim().isEmpty()) {
            return ResponseMessage.fail("Option C is required", null);
        }
        if (dto.getOptionD() == null || dto.getOptionD().trim().isEmpty()) {
            return ResponseMessage.fail("Option D is required", null);
        }
        if (dto.getCorrectAnswer() == null) {
            return ResponseMessage.fail("Correct answer is required (A, B, C or D)", null);
        }
        return ResponseMessage.success("OK", null);
    }
}
