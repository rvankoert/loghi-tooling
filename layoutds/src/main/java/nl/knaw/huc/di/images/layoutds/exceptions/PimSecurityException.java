package nl.knaw.huc.di.images.layoutds.exceptions;

public class PimSecurityException extends Exception {
    public PimSecurityException() {
        this("User does not have enough permissions");
    }

    public PimSecurityException(String message) {
        super(message);
    }
}
