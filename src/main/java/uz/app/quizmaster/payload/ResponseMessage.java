package uz.app.quizmaster.payload;

public record ResponseMessage(boolean success, String message, Object data) {
}
