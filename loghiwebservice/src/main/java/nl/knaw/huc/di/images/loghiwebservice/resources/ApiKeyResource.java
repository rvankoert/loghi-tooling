package nl.knaw.huc.di.images.loghiwebservice.resources;

import nl.knaw.huc.di.images.loghiwebservice.authentication.SessionManager;
import nl.knaw.huc.di.images.loghiwebservice.authentication.User;
import nl.knaw.huc.di.images.loghiwebservice.authentication.apikey.JsonApiKeyUserNameManager;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

@Path("apikey")
public class ApiKeyResource {
    private final SessionManager sessionManager;
    private final JsonApiKeyUserNameManager jsonStringApiKeyManager;

    public ApiKeyResource(SessionManager sessionManager, JsonApiKeyUserNameManager jsonStringApiKeyManager) {
        this.sessionManager = sessionManager;
        this.jsonStringApiKeyManager = jsonStringApiKeyManager;
    }

    @POST
    @Path("login")
    public Response login(@HeaderParam(HttpHeaders.AUTHORIZATION) UUID authorization) {
        if (authorization == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        final Optional<User> userByApiKey = jsonStringApiKeyManager.getUserByApiKey(authorization);

        if (userByApiKey.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UUID sessionId = UUID.randomUUID();

        sessionManager.register(sessionId, userByApiKey.get());

        return Response.noContent().header("X_AUTH_TOKEN", sessionId).build();
    }
}
