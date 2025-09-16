package uz.app.quizmaster.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AttemptDto {
    private Integer id;             // attempt ID
    private Integer quizId;         // faqat quiz ID
    private String quizTitle;       // studentga quiz nomi chiqsin
    private LocalDateTime startedAt;
    private LocalDateTime deadline;
    private LocalDateTime finishedAt;
    private Integer score;
    private Boolean cheatingDetected;

    // student answers
    private List<AnswerDto> answers;
}
