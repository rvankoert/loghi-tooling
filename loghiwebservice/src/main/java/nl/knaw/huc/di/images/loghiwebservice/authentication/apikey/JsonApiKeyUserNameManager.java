package nl.knaw.huc.di.images.loghiwebservice.authentication.apikey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import nl.knaw.huc.di.images.loghiwebservice.authentication.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JsonApiKeyUserNameManager {

    private final Map<UUID, String> apiKeyUserNameMap;

    public JsonApiKeyUserNameManager(String jsonString) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        final TypeFactory typeFactory = TypeFactory.defaultInstance();
        apiKeyUserNameMap = objectMapper.readValue(jsonString, typeFactory.constructMapType(HashMap.class, UUID.class, String.class));
    }

    public Optional<User> getUserByApiKey(UUID apiKey){
        final String userName = apiKeyUserNameMap.getOrDefault(apiKey, null);
        if (userName == null){
            return Optional.empty();
        }

        return Optional.of(new User(userName));
    }
}
