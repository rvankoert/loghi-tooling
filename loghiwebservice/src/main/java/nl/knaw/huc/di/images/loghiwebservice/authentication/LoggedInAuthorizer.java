package nl.knaw.huc.di.images.loghiwebservice.authentication;

import io.dropwizard.auth.Authorizer;

import java.security.Principal;

public class LoggedInAuthorizer implements Authorizer<User> {
    public LoggedInAuthorizer() {

    }

    @Override
    public boolean authorize(User principal, String s) {
        return principal != null;
    }
}
