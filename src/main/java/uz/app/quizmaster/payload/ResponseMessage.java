package uz.app.quizmaster.payload;

public record ResponseMessage<T>(boolean success, String message, T data) {

    public static <T> ResponseMessage<T> success(String message, T data) {
        return new ResponseMessage<>(true, message, data);
    }

    public static <T> ResponseMessage<T> fail(String message, T data) {
        return new ResponseMessage<>(false, message, data);
    }
}
