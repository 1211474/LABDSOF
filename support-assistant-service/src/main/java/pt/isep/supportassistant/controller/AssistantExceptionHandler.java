package pt.isep.supportassistant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pt.isep.supportassistant.dto.FallbackResponse;
import pt.isep.supportassistant.service.AssistantUnavailableException;

@RestControllerAdvice
public class AssistantExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssistantExceptionHandler.class);

    @ExceptionHandler(AssistantUnavailableException.class)
    public ResponseEntity<FallbackResponse> handleAssistantUnavailable(AssistantUnavailableException exception) {
        LOGGER.warn("Support Assistant could not retrieve verified service information ({})",
                exception.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new FallbackResponse(
                        "O assistente está temporariamente indisponível. Use a pesquisa do diretório.",
                        "/api/v1/services",
                        true
                ));
    }
}
