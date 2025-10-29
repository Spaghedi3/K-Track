package com.danis.ktrack.service.validation;

import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> errors;


    public ValidationException(List<String> errors) {
        super("Validation failed with " + errors.size() + " error(s): " + String.join(", ", errors) + ".");
        this.errors = errors;
    }


    public List<String> getErrors() {
        return errors;
    }
}
