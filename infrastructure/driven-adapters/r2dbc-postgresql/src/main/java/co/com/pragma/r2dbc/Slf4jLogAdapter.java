package co.com.pragma.r2dbc;

import co.com.pragma.model.application.gateways.LogPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLogAdapter implements LogPort {
    private final Logger logger;

    public Slf4jLogAdapter(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void warn(String message) { logger.warn(message); }
}

