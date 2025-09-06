package uz.app.quizmaster.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage {
    private String type;   // QUIZ_STARTED, ANSWER_SUBMITTED, QUIZ_FINISHED
    private String sender;
    private String content;
}
