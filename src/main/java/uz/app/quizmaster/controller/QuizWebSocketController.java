package uz.app.quizmaster.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import uz.app.quizmaster.payload.WebSocketMessage;

import java.security.Principal;
import java.time.Instant;

@Controller
@RequiredArgsConstructor
@Slf4j
public class QuizWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Student → answer yuboradi → Teacher ko‘radi.
     * Frontend: send to /app/answer
     * Broadcast: /topic/quiz/{quizId}/answers
     */
    @MessageMapping("/answer")
    public void handleAnswer(@Payload WebSocketMessage message, Principal principal) {
        try {
            String sender = (principal != null) ? principal.getName() : message.sender();

            WebSocketMessage enriched = new WebSocketMessage(
                    "ANSWER_SUBMITTED",
                    sender,
                    message.content(),
                    message.quizId(),
                    message.questionId(),
                    Instant.now().toString()
            );

            if (message.quizId() != null) {
                messagingTemplate.convertAndSend("/topic/quiz/" + message.quizId() + "/answers", enriched);
            } else {
                messagingTemplate.convertAndSend("/topic/answers", enriched);
            }

            log.info("Answer forwarded: quizId={}, sender={}", message.quizId(), sender);
        } catch (Exception ex) {
            log.error("❌ Error in handleAnswer", ex);
        }
    }

    /**
     * Teacher quizni boshlaydi → barcha studentlarga.
     * Frontend: send to /app/start
     * Broadcast: /topic/quiz/{quizId}/events
     */
    @MessageMapping("/start")
    public void handleStart(@Payload WebSocketMessage message, Principal principal) {
        try {
            String sender = (principal != null) ? principal.getName() : message.sender();

            WebSocketMessage enriched = new WebSocketMessage(
                    "QUIZ_STARTED",
                    sender,
                    message.content(),
                    message.quizId(),
                    message.questionId(),
                    Instant.now().toString()
            );

            if (message.quizId() != null) {
                messagingTemplate.convertAndSend("/topic/quiz/" + message.quizId() + "/events", enriched);
            } else {
                messagingTemplate.convertAndSend("/topic/quiz/events", enriched);
            }

            log.info("Quiz started: quizId={}, sender={}", message.quizId(), sender);
        } catch (Exception ex) {
            log.error("❌ Error in handleStart", ex);
        }
    }

    /**
     * Teacher quizni tugatadi → barcha studentlarga.
     * Frontend: send to /app/finish
     * Broadcast: /topic/quiz/{quizId}/events
     */
    @MessageMapping("/finish")
    public void handleFinish(@Payload WebSocketMessage message, Principal principal) {
        try {
            String sender = (principal != null) ? principal.getName() : message.sender();

            WebSocketMessage enriched = new WebSocketMessage(
                    "QUIZ_FINISHED",
                    sender,
                    message.content(),
                    message.quizId(),
                    message.questionId(),
                    Instant.now().toString()
            );

            if (message.quizId() != null) {
                messagingTemplate.convertAndSend("/topic/quiz/" + message.quizId() + "/events", enriched);
            } else {
                messagingTemplate.convertAndSend("/topic/quiz/events", enriched);
            }

            log.info("Quiz finished: quizId={}, sender={}", message.quizId(), sender);
        } catch (Exception ex) {
            log.error("❌ Error in handleFinish", ex);
        }
    }
}
