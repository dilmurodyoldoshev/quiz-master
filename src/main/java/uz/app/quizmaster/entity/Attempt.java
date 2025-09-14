package uz.app.quizmaster.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "attempts")
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startedAt;   // Quizni boshlagan vaqt

    private LocalDateTime finishedAt;  // Student tugatgan vaqt (agar null bo‘lsa, hali tugatmagan)

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer score;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean cheatingDetected;

    /**
     * Deadline hisoblash uchun yordamchi metod:
     * Quiz durationMinutes asosida attemptning oxirgi vaqti.
     */
    public LocalDateTime getDeadline() {
        if (quiz == null || quiz.getDurationMinutes() == null) {
            return null;
        }
        return startedAt.plusMinutes(quiz.getDurationMinutes());
    }

    /**
     * Studentning belgilangan vaqt ichida yakunlaganligini tekshirish.
     */
    public boolean isFinishedInTime() {
        LocalDateTime deadline = getDeadline();
        if (finishedAt == null || deadline == null) return false;
        return !finishedAt.isAfter(deadline);
    }
}
