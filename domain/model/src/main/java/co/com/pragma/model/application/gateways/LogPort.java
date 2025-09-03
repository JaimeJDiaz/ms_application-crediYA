package co.com.pragma.model.application.gateways;

public interface LogPort {
    void info(String message);
    void debug(String message);
    void error(String message);
}
