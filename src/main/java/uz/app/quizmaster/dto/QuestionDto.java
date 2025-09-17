package uz.app.quizmaster.dto;

import lombok.Data;
import uz.app.quizmaster.enums.AnswerType;

@Data
public class QuestionDto {
    private Integer id;
    private String text;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private AnswerType correctAnswer;
}
