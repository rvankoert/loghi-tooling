package nl.knaw.huc.di.images.loghiwebservice.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.setup.Environment;
import nl.knaw.huc.di.images.loghiwebservice.authentication.LoggedInAuthorizer;
import nl.knaw.huc.di.images.loghiwebservice.authentication.SessionManager;
import nl.knaw.huc.di.images.loghiwebservice.authentication.User;
import nl.knaw.huc.di.images.loghiwebservice.authentication.apikey.ApiKeyAuthenticator;
import nl.knaw.huc.di.images.loghiwebservice.authentication.apikey.ApiKeyFilter;
import nl.knaw.huc.di.images.loghiwebservice.authentication.apikey.JsonApiKeyUserNameManager;
import nl.knaw.huc.di.images.loghiwebservice.resources.ApiKeyResource;

public class SecurityConfig {
    private final boolean enabled;
    private final String securityJsonString;

    @JsonCreator
    public SecurityConfig(@JsonProperty("enabled") boolean enabled, @JsonProperty("securityJsonString") String securityJsonString) {
        this.enabled = enabled;
        this.securityJsonString = securityJsonString;
    }

    public void registerSecurity(Environment environment) {
        if (enabled) {
            final SessionManager sessionManager = new SessionManager();
            final JsonApiKeyUserNameManager jsonStringApiKeyManager;
            try {
                jsonStringApiKeyManager = new JsonApiKeyUserNameManager(securityJsonString);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            environment.jersey().register(new ApiKeyResource(sessionManager, jsonStringApiKeyManager));
            environment.jersey().register(new AuthDynamicFeature(new ApiKeyFilter.Builder<User>()
                    .setAuthenticator(new ApiKeyAuthenticator(sessionManager))
                    .setAuthorizer(new LoggedInAuthorizer())
                    .setPrefix("ApiKey")
                    .setRealm("Local Api Key")
                    .buildAuthFilter()
            ));
        }
    }
}
