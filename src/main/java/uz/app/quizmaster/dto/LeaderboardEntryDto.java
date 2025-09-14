package uz.app.quizmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardEntryDto {
    private String username;   // Foydalanuvchi nomi
    private Integer score;     // Natija
    private Integer rank;      // O‘rin
}
