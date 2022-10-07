package nl.knaw.huc.di.images.layoutds.exceptions;

import java.util.Set;

public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Set<String> errors) {
        super("" + errors);
    }
}
