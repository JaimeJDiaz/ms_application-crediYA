package co.com.pragma.usecase.application.exception;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
