package co.com.pragma.usecase.application;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.LoanType;
import co.com.pragma.usecase.application.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationValidatorTest {
    private ApplicationValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ApplicationValidator();
    }

    @Test
    void validateFields_nullApplication_throwsException() {
        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validateFields(null));
        assertTrue(ex.getErrors().contains("Application is required"));
    }

    @Test
    void validateFields_nullAmount_throwsException() {
        Application app = new Application();
        app.setType(1L);
        app.setTerm(12);
        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validateFields(app));
        assertTrue(ex.getErrors().contains("Amount is required"));
    }

    @Test
    void validateFields_nullType_throwsException() {
        Application app = new Application();
        app.setAmount(BigDecimal.TEN);
        app.setTerm(12);
        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validateFields(app));
        assertTrue(ex.getErrors().contains("Type is required"));
    }

    @Test
    void validateFields_nullTerm_throwsException() {
        Application app = new Application();
        app.setAmount(BigDecimal.TEN);
        app.setType(1L);
        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validateFields(app));
        assertTrue(ex.getErrors().contains("Term is required"));
    }

    @Test
    void validateFields_allValid_noException() {
        Application app = new Application();
        app.setAmount(BigDecimal.TEN);
        app.setType(1L);
        app.setTerm(12);
        assertDoesNotThrow(() -> validator.validateFields(app));
    }

    @Test
    void validateAmount_belowMin_throwsException() {
        Application app = new Application();
        app.setAmount(BigDecimal.valueOf(500));
        LoanType type = new LoanType();
        type.setMinAmount(BigDecimal.valueOf(1000));
        type.setMaxAmount(BigDecimal.valueOf(5000));
        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validateAmount(app, type));
        assertTrue(ex.getErrors().contains("Amount out of range"));
    }

    @Test
    void validateAmount_aboveMax_throwsException() {
        Application app = new Application();
        app.setAmount(BigDecimal.valueOf(6000));
        LoanType type = new LoanType();
        type.setMinAmount(BigDecimal.valueOf(1000));
        type.setMaxAmount(BigDecimal.valueOf(5000));
        ValidationException ex = assertThrows(ValidationException.class, () -> validator.validateAmount(app, type));
        assertTrue(ex.getErrors().contains("Amount out of range"));
    }

    @Test
    void validateAmount_inRange_noException() {
        Application app = new Application();
        app.setAmount(BigDecimal.valueOf(3000));
        LoanType type = new LoanType();
        type.setMinAmount(BigDecimal.valueOf(1000));
        type.setMaxAmount(BigDecimal.valueOf(5000));
        assertDoesNotThrow(() -> validator.validateAmount(app, type));
    }
}