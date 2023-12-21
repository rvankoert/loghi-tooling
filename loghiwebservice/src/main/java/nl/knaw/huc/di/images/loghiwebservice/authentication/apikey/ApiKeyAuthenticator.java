package nl.knaw.huc.di.images.loghiwebservice.authentication.apikey;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import nl.knaw.huc.di.images.loghiwebservice.authentication.SessionManager;
import nl.knaw.huc.di.images.loghiwebservice.authentication.User;

import java.util.Optional;
import java.util.UUID;

public class ApiKeyAuthenticator implements Authenticator<UUID, User> {
    private final SessionManager sessionManager;

    public ApiKeyAuthenticator(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Optional<User> authenticate(UUID sessionId) throws AuthenticationException {
        return sessionManager.getUserBySession(sessionId);
    }
}



