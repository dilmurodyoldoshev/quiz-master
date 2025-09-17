package uz.app.quizmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto {
    private Integer quizId;
    private String quizTitle;
    private Integer score;
    private LocalDateTime completedAt;
    private Integer rank; // optional, agar hisoblasangiz to'ldiriladi
}
