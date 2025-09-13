package uz.app.quizmaster.dto;

import lombok.Data;

@Data
public class QuestionDto {
    private String text;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
}
