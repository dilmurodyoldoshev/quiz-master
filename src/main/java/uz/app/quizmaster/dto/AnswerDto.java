package uz.app.quizmaster.dto;

import lombok.Data;

@Data
public class AnswerDto {
    private Integer questionId;
    private String questionText;
    private String selectedOption;
    private Boolean isCorrect;
}
