package co.com.pragma.sqs.sender.exception;

public class SqsSerializationException extends RuntimeException {
    public SqsSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}

