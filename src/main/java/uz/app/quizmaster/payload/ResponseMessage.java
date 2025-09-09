package uz.app.quizmaster.payload;

public record ResponseMessage(boolean success, String message, Object data) {
    public static ResponseMessage success(String message, Object data) {
        return new ResponseMessage(true, message, data);
    }
}
