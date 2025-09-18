package co.com.pragma.exceptionhandler;

import co.com.pragma.sqs.sender.exception.SqsSerializationException;
import co.com.pragma.usecase.application.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(ValidationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", ex.getErrors());

        return Mono.just(ResponseEntity.badRequest().body(body));
    }

    @ExceptionHandler(SqsSerializationException.class)
    public Mono<ResponseEntity<String>> handleSqsSerializationException(SqsSerializationException ex) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error serializing message for SQS: " + ex.getMessage())
        );
    }
}
