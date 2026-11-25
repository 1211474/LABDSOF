package pt.isep.supportassistant.integration;

public class LlmCompletionException extends RuntimeException {

    public LlmCompletionException(String message, Throwable cause) {
        super(message, cause);
    }

    public LlmCompletionException(String message) {
        super(message);
    }
}
