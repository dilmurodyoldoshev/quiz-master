package uz.app.quizmaster.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String text;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    @Column(nullable = false)
    private String correctAnswer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "quiz_id")
    @JsonIgnoreProperties({"questions"})
    private Quiz quiz;


    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    private User createdBy;
}
