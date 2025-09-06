package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import uz.app.quizmaster.dto.WebSocketMessage;

@Controller
@RequiredArgsConstructor
public class QuizWebSocketController {

    // Student → javob yuboradi → Teacher ko‘radi
    @MessageMapping("/answer")
    @SendTo("/topic/answers")
    public WebSocketMessage handleAnswer(WebSocketMessage message) {
        message.setType("ANSWER_SUBMITTED");
        return message;
    }

    // Teacher → quizni boshlaydi → barcha studentlarga xabar
    @MessageMapping("/start")
    @SendTo("/topic/quiz")
    public WebSocketMessage handleStart(WebSocketMessage message) {
        message.setType("QUIZ_STARTED");
        return message;
    }

    // Teacher → quizni tugatadi → barcha studentlarga xabar
    @MessageMapping("/finish")
    @SendTo("/topic/quiz")
    public WebSocketMessage handleFinish(WebSocketMessage message) {
        message.setType("QUIZ_FINISHED");
        return message;
    }
}
