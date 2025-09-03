package uz.app.quizmaster.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResultDto {
    private Integer quizId;
    private Integer userId;
    private Integer score;
    private Integer rank;
    private LocalDateTime completedAt;
}
