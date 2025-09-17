package uz.app.quizmaster.dto;

import lombok.Data;

@Data
public class QuizDto {
    private Integer id;
    private String title;
    private String description;
    private Boolean cheatingControl = false;
    private Integer durationMinutes;
}
