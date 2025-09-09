package uz.app.quizmaster.payload;

public record WebSocketMessage(
        String type,        // QUIZ_STARTED, ANSWER_SUBMITTED, QUIZ_FINISHED
        String sender,      // kim yubordi
        String content,     // asosiy message yoki javob
        Integer quizId,     // quizga tegishli bo‘lsa
        Integer questionId, // questionga tegishli bo‘lsa
        String timestamp    // ISO-8601 time
) {}
