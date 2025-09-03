package uz.app.quizmaster.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttemptDto {
    private Integer quizId;
    private Integer userId;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer score;
    private Boolean cheatingDetected;
}
