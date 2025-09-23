package uz.app.quizmaster.dto;

import lombok.Data;
import uz.app.quizmaster.enums.AnswerType;

@Data
public class AnswerRequestDto {
    private AnswerType selectedOption;
}
