package co.com.pragma.usecase.application;

import co.com.pragma.model.application.Application;
import co.com.pragma.model.application.LoanType;
import co.com.pragma.usecase.application.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class ApplicationValidator {

    public void validateFields(Application application) {
        List<String> errors = new ArrayList<>();

        if (application == null) {
            errors.add("Application is required");
        } else {
            validateNotNull(application.getAmount(), "Amount is required", errors);
            validateNotNull(application.getType(), "Type is required", errors);
            validateNotNull(application.getTerm(), "Term is required", errors);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateNotNull(Object value, String message, List<String> errors) {
        if (value == null) {
            errors.add(message);
        }
    }

    public void validateAmount(Application application, LoanType type) {
        if (application.getAmount().compareTo(type.getMinAmount()) < 0 || application.getAmount().compareTo(type.getMaxAmount()) > 0) {
            throw new ValidationException(List.of("Amount out of range"));
        }
    }
}
