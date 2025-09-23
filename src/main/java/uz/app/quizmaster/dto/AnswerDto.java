package uz.app.quizmaster.dto;

import lombok.Data;
import uz.app.quizmaster.enums.AnswerType;

@Data
public class AnswerDto {
    private Integer questionId;
    private String questionText;
    private AnswerType selectedOption;
    private Boolean isCorrect;
}
