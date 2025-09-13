package uz.app.quizmaster.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizDto {
    private String title;
    private String description;
    private Boolean cheatingControl = false;
    private Integer durationMinutes;
}
